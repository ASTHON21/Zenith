package com.example.data.export

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.model.Note
import com.example.data.preferences.EditorFontVoice
import com.example.data.preferences.ZenithPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExportManager {

    private sealed class ScrollBlock(val height: Float) {
        class SpacerBlock(h: Float) : ScrollBlock(h)
        class TextBlock(val layout: StaticLayout, val isQuote: Boolean = false) : ScrollBlock(layout.height.toFloat())
    }

    private fun getExportsDir(context: Context): File {
        val dir = File(context.cacheDir, "exports")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun sanitizeFileName(raw: String, fallback: String): String {
        val cleaned = raw.replace("[^a-zA-Z0-9._ -]".toRegex(), "").trim().replace("\\s+".toRegex(), "_")
        return if (cleaned.isNotEmpty()) cleaned.take(48) else fallback
    }

    private fun getUriForFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun launchShareSheet(context: Context, uri: Uri, mimeType: String, chooserTitle: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, chooserTitle).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    suspend fun exportTypesetPdf(
        context: Context,
        note: Note,
        preferences: ZenithPreferences
    ): Uri = withContext(Dispatchers.IO) {
        val pageWidth = 595
        val pageHeight = 842
        val marginX = 64f
        val marginTop = 72f
        val marginBottom = 72f
        val contentWidth = (pageWidth - marginX * 2).toInt()

        val serifTypeface = ResourcesCompat.getFont(context, R.font.newsreader) ?: Typeface.SERIF
        val sansTypeface = ResourcesCompat.getFont(context, R.font.inter) ?: Typeface.SANS_SERIF
        val bodyTypeface = if (preferences.fontVoice == EditorFontVoice.SERIF) serifTypeface else sansTypeface

        val tone = preferences.atmosphere
        val bgColor = tone.background.toArgb()
        val inkColor = tone.ink.toArgb()
        val mutedColor = tone.mutedInk.toArgb()
        val borderColor = tone.border.toArgb()

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = serifTypeface
            textSize = 22f
            color = inkColor
        }
        val metaPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = sansTypeface
            textSize = 9.5f
            color = mutedColor
            letterSpacing = 0.08f
        }
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = bodyTypeface
            textSize = 11.5f
            color = inkColor
        }
        val quotePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(serifTypeface, Typeface.ITALIC)
            textSize = 12f
            color = mutedColor
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            strokeWidth = 1f
        }
        val accentBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = inkColor
            strokeWidth = 2f
        }

        val pdf = PdfDocument()
        var pageNumber = 1
        var page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas
        canvas.drawColor(bgColor)
        var y = marginTop

        fun startNewPage() {
            // Draw page number on finished page
            val footerText = "— $pageNumber —"
            val footerWidth = metaPaint.measureText(footerText)
            canvas.drawText(footerText, (pageWidth - footerWidth) / 2f, pageHeight - 36f, metaPaint)
            pdf.finishPage(page)

            pageNumber++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page.canvas
            canvas.drawColor(bgColor)
            y = marginTop
        }

        fun drawLayout(text: String, paint: TextPaint, width: Int, leftOffset: Float = 0f, spacingMult: Float = preferences.lineSpacing.multiplier) {
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, spacingMult)
                .build()

            if (y + layout.height > pageHeight - marginBottom) {
                startNewPage()
            }
            canvas.save()
            canvas.translate(marginX + leftOffset, y)
            layout.draw(canvas)
            canvas.restore()
            y += layout.height
        }

        // Metadata header
        val dateStr = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Date(note.updatedAt))
        val metaLine = "${note.notebook.uppercase(Locale.US)}  ·  $dateStr  ·  ${note.wordCount} WORDS"
        drawLayout(metaLine, metaPaint, contentWidth, spacingMult = 1.2f)
        y += 12f

        // Title
        val safeTitle = note.title.ifBlank { "Untitled Thought" }
        drawLayout(safeTitle, titlePaint, contentWidth, spacingMult = 1.25f)
        y += 12f

        canvas.drawLine(marginX, y, pageWidth - marginX, y, borderPaint)
        y += 22f

        // Body lines
        val lines = note.content.lines()
        for (rawLine in lines) {
            val trimmed = rawLine.trim()
            when {
                trimmed.isEmpty() -> y += 10f
                trimmed.startsWith("> ") -> {
                    val quoteText = trimmed.removePrefix("> ")
                    val quoteWidth = contentWidth - 18
                    val layout = StaticLayout.Builder.obtain(quoteText, 0, quoteText.length, quotePaint, quoteWidth)
                        .setLineSpacing(0f, 1.55f)
                        .build()
                    if (y + layout.height + 12f > pageHeight - marginBottom) {
                        startNewPage()
                    }
                    canvas.drawLine(marginX + 4f, y + 2f, marginX + 4f, y + layout.height - 2f, accentBarPaint)
                    canvas.save()
                    canvas.translate(marginX + 16f, y)
                    layout.draw(canvas)
                    canvas.restore()
                    y += layout.height + 12f
                }
                trimmed.startsWith("- [x] ") || trimmed.startsWith("- [X] ") -> {
                    val itemText = "☑  ${trimmed.substring(6)}"
                    drawLayout(itemText, bodyPaint, contentWidth - 8, leftOffset = 4f, spacingMult = 1.45f)
                    y += 6f
                }
                trimmed.startsWith("- [ ] ") -> {
                    val itemText = "☐  ${trimmed.removePrefix("- [ ] ")}"
                    drawLayout(itemText, bodyPaint, contentWidth - 8, leftOffset = 4f, spacingMult = 1.45f)
                    y += 6f
                }
                else -> {
                    val clean = trimmed.removePrefix("### ").removePrefix("## ").removePrefix("# ")
                    drawLayout(clean, bodyPaint, contentWidth)
                    y += 10f
                }
            }
        }

        // Last page footer
        val footerText = "— $pageNumber —"
        val footerWidth = metaPaint.measureText(footerText)
        canvas.drawText(footerText, (pageWidth - footerWidth) / 2f, pageHeight - 36f, metaPaint)
        pdf.finishPage(page)

        val fileName = "${sanitizeFileName(safeTitle, "zenith_note")}.pdf"
        val outFile = File(getExportsDir(context), fileName)
        FileOutputStream(outFile).use { pdf.writeTo(it) }
        pdf.close()

        getUriForFile(context, outFile)
    }

    suspend fun exportMarkdownZip(
        context: Context,
        notes: List<Note>
    ): Uri = withContext(Dispatchers.IO) {
        val dateTag = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        val outFile = File(getExportsDir(context), "Zenith_Archive_$dateTag.zip")
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

        ZipOutputStream(FileOutputStream(outFile)).use { zip ->
            val usedNames = mutableSetOf<String>()
            notes.filterNot { it.isArchived }.forEach { note ->
                val folder = sanitizeFileName(note.notebook, "Inbox")
                val baseTitle = sanitizeFileName(note.title, "Untitled_${note.id}")
                var entryPath = "$folder/$baseTitle.md"
                var suffix = 2
                while (!usedNames.add(entryPath)) {
                    entryPath = "$folder/${baseTitle}_$suffix.md"
                    suffix++
                }

                val markdown = buildString {
                    appendLine("---")
                    appendLine("title: \"${note.title.replace("\"", "'")}\"")
                    appendLine("notebook: \"${note.notebook}\"")
                    appendLine("status: \"${note.status}\"")
                    appendLine("tags: [${note.tags.joinToString(", ") { "\"$it\"" }}]")
                    appendLine("pinned: ${note.isPinned}")
                    appendLine("updated_at: \"${isoFormat.format(Date(note.updatedAt))}\"")
                    appendLine("---")
                    appendLine()
                    if (note.title.isNotBlank()) {
                        appendLine("# ${note.title}")
                        appendLine()
                    }
                    append(note.content)
                    appendLine()
                }

                zip.putNextEntry(ZipEntry(entryPath))
                zip.write(markdown.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
        }

        getUriForFile(context, outFile)
    }

    suspend fun exportLongScrollImage(
        context: Context,
        note: Note,
        preferences: ZenithPreferences
    ): Uri = withContext(Dispatchers.IO) {
        val imgWidth = 1080
        val padX = 96f
        val topPad = 120f
        val bottomPad = 120f
        val contentWidth = (imgWidth - padX * 2).toInt()

        val serifTypeface = ResourcesCompat.getFont(context, R.font.newsreader) ?: Typeface.SERIF
        val sansTypeface = ResourcesCompat.getFont(context, R.font.inter) ?: Typeface.SANS_SERIF
        val bodyTypeface = if (preferences.fontVoice == EditorFontVoice.SERIF) serifTypeface else sansTypeface

        val tone = preferences.atmosphere
        val bgColor = tone.background.toArgb()
        val inkColor = tone.ink.toArgb()
        val mutedColor = tone.mutedInk.toArgb()
        val borderColor = tone.border.toArgb()

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = serifTypeface
            textSize = 54f
            color = inkColor
        }
        val metaPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = sansTypeface
            textSize = 24f
            color = mutedColor
            letterSpacing = 0.08f
        }
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = bodyTypeface
            textSize = (preferences.bodyFontSizeSp * 2.2f)
            color = inkColor
        }
        val quotePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(serifTypeface, Typeface.ITALIC)
            textSize = (preferences.bodyFontSizeSp * 2.25f)
            color = mutedColor
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            strokeWidth = 2f
        }
        val accentBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = inkColor
            strokeWidth = 5f
        }

        val dateStr = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(Date(note.updatedAt))
        val metaText = "${note.notebook.uppercase(Locale.US)}  ·  $dateStr  ·  ${note.wordCount} WORDS"
        val safeTitle = note.title.ifBlank { "Untitled Thought" }

        val metaLayout = StaticLayout.Builder.obtain(metaText, 0, metaText.length, metaPaint, contentWidth)
            .setLineSpacing(0f, 1.2f).build()
        val titleLayout = StaticLayout.Builder.obtain(safeTitle, 0, safeTitle.length, titlePaint, contentWidth)
            .setLineSpacing(0f, 1.25f).build()

        val blocks = mutableListOf<ScrollBlock>()
        note.content.lines().forEach { rawLine ->
            val trimmed = rawLine.trim()
            when {
                trimmed.isEmpty() -> blocks.add(ScrollBlock.SpacerBlock(26f))
                trimmed.startsWith("> ") -> {
                    val qText = trimmed.removePrefix("> ")
                    val layout = StaticLayout.Builder.obtain(qText, 0, qText.length, quotePaint, contentWidth - 40)
                        .setLineSpacing(0f, 1.55f).build()
                    blocks.add(ScrollBlock.TextBlock(layout, isQuote = true))
                    blocks.add(ScrollBlock.SpacerBlock(28f))
                }
                trimmed.startsWith("- [x] ") || trimmed.startsWith("- [X] ") -> {
                    val cText = "☑  ${trimmed.substring(6)}"
                    val layout = StaticLayout.Builder.obtain(cText, 0, cText.length, bodyPaint, contentWidth)
                        .setLineSpacing(0f, 1.45f).build()
                    blocks.add(ScrollBlock.TextBlock(layout))
                    blocks.add(ScrollBlock.SpacerBlock(16f))
                }
                trimmed.startsWith("- [ ] ") -> {
                    val cText = "☐  ${trimmed.removePrefix("- [ ] ")}"
                    val layout = StaticLayout.Builder.obtain(cText, 0, cText.length, bodyPaint, contentWidth)
                        .setLineSpacing(0f, 1.45f).build()
                    blocks.add(ScrollBlock.TextBlock(layout))
                    blocks.add(ScrollBlock.SpacerBlock(16f))
                }
                else -> {
                    val clean = trimmed.removePrefix("### ").removePrefix("## ").removePrefix("# ")
                    val layout = StaticLayout.Builder.obtain(clean, 0, clean.length, bodyPaint, contentWidth)
                        .setLineSpacing(0f, preferences.lineSpacing.multiplier).build()
                    blocks.add(ScrollBlock.TextBlock(layout))
                    blocks.add(ScrollBlock.SpacerBlock(22f))
                }
            }
        }

        val totalBodyHeight = blocks.sumOf { it.height.toDouble() }.toFloat()
        val totalHeight = (topPad + metaLayout.height + 28f + titleLayout.height + 48f + totalBodyHeight + 110f + bottomPad)
            .toInt().coerceIn(900, 12000)

        val bitmap = Bitmap.createBitmap(imgWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(bgColor)

        var y = topPad
        canvas.save()
        canvas.translate(padX, y)
        metaLayout.draw(canvas)
        canvas.restore()
        y += metaLayout.height + 28f

        canvas.save()
        canvas.translate(padX, y)
        titleLayout.draw(canvas)
        canvas.restore()
        y += titleLayout.height + 28f

        canvas.drawLine(padX, y, imgWidth - padX, y, borderPaint)
        y += 36f

        for (block in blocks) {
            when (block) {
                is ScrollBlock.SpacerBlock -> y += block.height
                is ScrollBlock.TextBlock -> {
                    if (block.isQuote) {
                        canvas.drawLine(padX + 8f, y + 6f, padX + 8f, y + block.height - 6f, accentBarPaint)
                        canvas.save()
                        canvas.translate(padX + 36f, y)
                        block.layout.draw(canvas)
                        canvas.restore()
                    } else {
                        canvas.save()
                        canvas.translate(padX, y)
                        block.layout.draw(canvas)
                        canvas.restore()
                    }
                    y += block.height
                }
            }
        }

        // Colophon footer
        y += 40f
        canvas.drawLine(imgWidth / 2f - 40f, y, imgWidth / 2f + 40f, y, borderPaint)
        y += 38f
        val colophon = "ZENITH · MA (SPACE) · ARCHIVAL SCROLL"
        val colophonWidth = metaPaint.measureText(colophon)
        canvas.drawText(colophon, (imgWidth - colophonWidth) / 2f, y, metaPaint)

        val fileName = "${sanitizeFileName(safeTitle, "zenith_scroll")}.png"
        val outFile = File(getExportsDir(context), fileName)
        FileOutputStream(outFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        bitmap.recycle()

        getUriForFile(context, outFile)
    }
}
