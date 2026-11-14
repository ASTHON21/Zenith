---
name: Zenith Minimalist Notes
colors:
  surface: '#faf9f6'
  surface-dim: '#dbdad7'
  surface-bright: '#faf9f6'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f4f3f0'
  surface-container: '#efeeeb'
  surface-container-high: '#e9e8e5'
  surface-container-highest: '#e3e2df'
  on-surface: '#1b1c1a'
  on-surface-variant: '#444748'
  inverse-surface: '#2f312f'
  inverse-on-surface: '#f2f1ee'
  outline: '#747878'
  outline-variant: '#c4c7c7'
  surface-tint: '#5f5e5e'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#1c1b1b'
  on-primary-container: '#858383'
  inverse-primary: '#c8c6c5'
  secondary: '#605e5b'
  on-secondary: '#ffffff'
  secondary-container: '#e5e2dd'
  on-secondary-container: '#666461'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#1b1c1c'
  on-tertiary-container: '#858383'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e5e2e1'
  primary-fixed-dim: '#c8c6c5'
  on-primary-fixed: '#1c1b1b'
  on-primary-fixed-variant: '#474746'
  secondary-fixed: '#e5e2dd'
  secondary-fixed-dim: '#c9c6c1'
  on-secondary-fixed: '#1c1c19'
  on-secondary-fixed-variant: '#484743'
  tertiary-fixed: '#e4e2e1'
  tertiary-fixed-dim: '#c8c6c5'
  on-tertiary-fixed: '#1b1c1c'
  on-tertiary-fixed-variant: '#474747'
  background: '#faf9f6'
  on-background: '#1b1c1a'
  surface-variant: '#e3e2df'
typography:
  headline-xl:
    fontFamily: Newsreader
    fontSize: 40px
    fontWeight: '400'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-xl-mobile:
    fontFamily: Newsreader
    fontSize: 30px
    fontWeight: '400'
    lineHeight: 38px
    letterSpacing: -0.015em
  headline-lg:
    fontFamily: Newsreader
    fontSize: 32px
    fontWeight: '400'
    lineHeight: 40px
    letterSpacing: -0.015em
  headline-lg-mobile:
    fontFamily: Newsreader
    fontSize: 24px
    fontWeight: '400'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Newsreader
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Newsreader
    fontSize: 18px
    fontWeight: '500'
    lineHeight: 24px
    letterSpacing: 0em
  body-lg:
    fontFamily: Newsreader
    fontSize: 19px
    fontWeight: '400'
    lineHeight: 32px
    letterSpacing: -0.005em
  body-md:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: -0.01em
  body-sm:
    fontFamily: Inter
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.04em
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
    letterSpacing: 0.06em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2.5rem
---

## Brand & Style

This design system embodies the confluence of Japanese *Ma* (negative space, the void that gives shape to meaning) and Swiss International Typographic Style (clarity, objective structure, and disciplined hierarchy). 

The target audience comprises writers, essayists, researchers, and mindful thinkers who seek solace from the sensory bombardment of modern software. The interface evokes quiet focus, stillness, and an analog permanence akin to raw, heavy archival paper and Sumi ink.

### Design Principles
- **Quietude over Utility Excess:** Functionality reveals itself only upon intent. Non-essential controls recede into total transparency.
- **The Dignity of Negative Space:** Whitespace is not empty space; it is structural architecture. Margins protect thoughts from digital claustrophobia.
- **Asymmetry and Balance:** Unadorned structural balance replaces superficial ornamentation. Depth is perceived through tonal subtlety, typographic weight, and disciplined alignment.

## Colors

The palette simulates natural materials: uncoated Japanese washi paper, charcoal, and aged ink. There are zero saturated chromatic accents; visual hierarchy relies exclusively on value, contrast, and weight.

### Palette Implementation
- **Base Canvas (`#F7F6F3` / `#FBFBFA`):** The primary paper substrate. Screens default to `#F7F6F3` for ambient body warmth, while active writing canvases utilize `#FBFBFA` for luminous legibility.
- **Surface Elevation (`#F2F0EC`):** Used sparingly for secondary floating controls, bottom sheets, or subtle grouping panels.
- **Primary Ink (`#1A1A1A`):** Deep charcoal ink, softened slightly from harsh absolute black to ease optical fatigue during long writing sessions.
- **Muted Charcoal (`#555555`):** Secondary body copy, timestamps, and low-priority meta-information.
- **Subtle Whisper Line (`#EAE8E3`):** 1px hairline structural dividers and input boundaries that fade into the background when not actively engaged.
- **Atmospheric Accent (`#8A8884` & `#2C2C2C`):** Subdued selection states, caret pulses, and focused navigation glyphs.

## Typography

The typographic hierarchy balances literary refinement with Swiss systemic clarity:
- **Newsreader** serves as the literary voice: deployed for note titles, essay headings, and the long-form immersive reading/writing mode (`body-lg`). Its proportions and humanist serifs ground the interface in physical bookmaking tradition.
- **Inter** fulfills the Swiss functional role: applied to all user interface mechanics, metadata, folders, toolbars, and search prompts. Set with tight tracking, neutral geometric precision, and unambiguous clarity.

For editorial reading and composition, line lengths must not exceed 65 characters per line. Heading margins must always feature disproportionate top spacing to signal clear conceptual breaks.

## Layout & Spacing

The layout is built upon an austere, fluid column structure governed by generous outer margins. 

### Rhythm & Alignment
- **Mobile Foundation:** A 4-column fluid layout with `1.5rem` (`24px`) safe margins on horizontal edges, granting the text ample breathing room away from physical device bezels.
- **Vertical Cadence:** Strict multiples of 4px and 8px govern line spacing and element stacking. Notes are anchored by generous top padding (`space-xl`), creating an unhurried, calm entry into the document.
- **Breakpoints:**
  - **Phone (< 600px):** Single column stream; full-bleed horizontal line dividers extending to margins; controls anchored to bottom thumb-zone.
  - **Tablet / Expanded (600px - 1024px):** Split two-pane layout (sidebar directory: 320px fixed, writing surface: fluid with max text measure of 680px centered).

## Elevation & Depth

This design system renounces skeuomorphic drop shadows and high-opacity blurs entirely. Spatial order is conveyed via tonal layering and hairline ink boundaries:

- **Level 0 (Base Paper):** `#F7F6F3` / `#FBFBFA`. Completely flat, non-elevated.
- **Level 1 (Surface Sheets & Modals):** `#F2F0EC` with a delicate `1px` border of `#EAE8E3`.
- **Level 2 (Floating Action / Ephemeral Overlays):** `#FBFBFA` backdrop supported by a soft, diffused ambient contact shadow: `box-shadow: 0 4px 20px -2px rgba(26, 26, 26, 0.04)`.
- **Active Focus:** Outlined with a single crisp `1px` stroke of `#2C2C2C`, eliminating heavy focus rings in favor of precision.

## Shapes

In keeping with architectural Swiss precision and the crisp cut of fine stationery, roundedness is restrained and architectural (`roundedness: 1`).

- **Standard Containers & Cards:** `0.25rem` (`4px`) radius. Subtle softening that avoids sharp severity without degenerating into playful bubbles.
- **Interactive Modals & Menus:** `0.5rem` (`8px`) radius (`rounded-lg`).
- **Pills / Status Chips:** Restricted exclusively to system counters or text tags where a `0.75rem` (`12px`) soft curve is permissible to visually separate structural panels from semantic labels.

## Components

### Buttons
- **Primary:** Background `#1A1A1A`, text `#F7F6F3`, border `none`, radius `4px`, padding `0.5rem 1rem`. Typography `label-md`.
- **Ghost / Minimal (Default for app actions):** Background `transparent`, text `#1A1A1A`, hover/active text `#555555`. No visible border until hovered or active.
- **Disabled State:** Opacity `0.3`, background `transparent`, text `#8A8884`.

### Input Fields & Editor Canvas
- **Editor Title Input:** Borderless, background `transparent`, typography `headline-lg-mobile` / `headline-lg`, placeholder `#8A8884`.
- **Editor Body Area:** Borderless, zero outline, zero background color. Continuous vertical scroll with relaxed line height (`body-lg`).
- **Search Bar:** Subtle `#F2F0EC` fill, `1px` border in `#EAE8E3`, inset padding `0.5rem 0.75rem`, typography `body-md`. Clear button appears as an ultra-fine icon.

### Cards & Note List Items
- **List Item:** Flat background `#F7F6F3`, separated by a single hairline divider `#EAE8E3` (`border-bottom: 1px solid #EAE8E3`). Hover/touch state shifts background to `#F2F0EC`.
- **Content Hierarchy:** Note Title (`headline-sm`, `#1A1A1A`), single-line snippet preview (`body-sm`, `#555555`), timestamp/tag (`label-sm`, `#8A8884`).

### Chips & Metadata Tags
- **Appearance:** Background `transparent`, border `1px solid #EAE8E3`, text `#555555`, radius `4px`, padding `2px 8px`. Typography `label-sm`. Active state switches border to `#2C2C2C` and text to `#1A1A1A`.

### Selection Controls (Checkboxes & Radios)
- **Checkboxes (Task Lists):** Small `16px x 16px` square, `1px` border of `#8A8884`, radius `2px`. Checked state: filled with `#1A1A1A` with a `#F7F6F3` hairline checkmark. Completed text strikes through with `#8A8884`.
- **Radio Buttons:** `16px x 16px` circle, `1px` border of `#8A8884`. Active state: filled center pip of `#1A1A1A` with `3px` white space buffer.

### Bottom Action Bar (Mobile Distraction-Free Toolbar)
- Fixed bottom panel, background `#F7F6F3` with `border-top: 1px solid #EAE8E3`. Accommodates minimal iconography (Word Count, Undo, Redo, Markdown Styler, Close). Unselected state `#8A8884`, active state `#1A1A1A`.