package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.Note
import com.example.data.model.TemporalBucket
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Zenith Notes", appName)
  }

  @Test
  fun `fresh database starts completely empty without sample data`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getDatabase(context)
    val initialNotes = db.noteDao().getAllNotes().first()
    val initialNotebooks = db.notebookDao().getAllNotebooks().first()
    assertTrue(initialNotes.isEmpty())
    assertTrue(initialNotebooks.isEmpty())
    assertEquals("Unfiled", Note().notebook)
  }

  @Test
  fun `verify note checklist parsing and prose extraction`() {
    val note = Note(
      id = 1,
      title = "Subtractive Design Checklist",
      content = """Editorial notes.

- [x] Clear desktop surface
- [x] Silence notifications
- [ ] Draft three essays"""
    )
    assertEquals(3, note.checklistItems.size)
    assertTrue(note.checklistItems[0].isChecked)
    assertTrue(note.checklistItems[1].isChecked)
    assertFalse(note.checklistItems[2].isChecked)
    assertEquals("Editorial notes.", note.proseContent)
    assertTrue(note.isChecklistFocused)
  }

  @Test
  fun `verify temporal bucketing`() {
    val now = System.currentTimeMillis()
    val todayNote = Note(id = 1, updatedAt = now)
    val weekNote = Note(id = 2, updatedAt = now - 3 * 86_400_000L)
    val earlierNote = Note(id = 3, updatedAt = now - 12 * 86_400_000L)

    assertEquals(TemporalBucket.TODAY, todayNote.temporalBucket)
    assertEquals(TemporalBucket.THIS_WEEK, weekNote.temporalBucket)
    assertEquals(TemporalBucket.EARLIER, earlierNote.temporalBucket)
  }
}
