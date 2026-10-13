# Handoff: Popcorn-Upsell-Karte (Kartenverkauf)

## Overview
A new **optional popcorn-ordering card** inserted into the existing KinoSoft cinema
ticket-sales flow (`Kartenverkauf`), positioned **between the seat-selection step
("Plätze wählen") and the payment step ("Kinokarten zahlen")**.

Its job is to let the guest add popcorn to their order without feeling pressured.
The card is deliberately a *gentle nudge*: it makes the offer visible and inviting,
gives a one-tap "popular choice" shortcut, but is trivial to ignore or remove. The
running payment total updates live to include popcorn.

## About the Design Files
The file in this bundle (`Kartenverkauf.dc.html`) is a **design reference created in
HTML** — a working prototype showing the intended look and behavior. It is **not
production code to copy directly**. It is authored as a "Design Component" (a streaming
HTML format) and uses a compiled daisyUI/Tailwind stylesheet from the KinoSoft design
system.

The task is to **recreate this design in the target codebase's existing environment**
(the real KinoSoft app — React/Vue/etc.) using its established components and patterns.
The HTML mock reproduces the existing flow only as context; **only the popcorn card and
the live total in the payment card are new.** Everything else already exists in the app.

## Fidelity
**High-fidelity.** Colors, typography, spacing, and component choices all come from the
KinoSoft design system (daisyUI v5 + Tailwind v4, theme `emerald`). Recreate it using the
app's existing daisyUI component classes and tokens rather than hand-written CSS. Do not
introduce new colors — every color used is an existing theme token.

## Screens / Views

### The flow (context)
A single vertically-stacked, centered column. Each step is a **step badge** (pill) followed
by a **card** (`card card-border shadow-md`, surface `#f9fafb` / `bg-gray-50`, `max-width: 28rem`,
full width below that). Steps, in order:

1. **Vorstellung wählen** — completed (green `badge-primary badge-soft` + checked checkbox)
2. **Kartenanzahl wählen** — completed
3. **Plätze wählen** — completed
4. **Popcorn dazu?** — NEW (see below)
5. **Kinokarten zahlen** — open (unchecked checkbox); shows the total + "Weiter zur Zahlung"

### NEW: Popcorn card

**Name:** Popcorn-Upsell-Karte

**Purpose:** Optionally add one or more portions of popcorn to the order.

**Step badge:** `badge badge-lg badge-accent badge-soft` (salmon/accent — *not* the green
`badge-primary` used by the required steps; this signals "optional / fun add-on"). Text:
`Popcorn dazu?` followed by a smaller, 60%-opacity label `optional`. No checkbox.

**Card surface:** identical to the other cards — `card card-border shadow-md`, background
`#f9fafb`. **The card surface is intentionally NOT tinted.** The accent (salmon) color is
used only as *targeted highlights* (badge, quick-add button, selected size button, the
"+ Weitere Portion" link). This keeps the card in the same visual family while still gently
standing out. (An earlier version tinted the whole card salmon — that was deliberately
walked back; do not tint the surface.)

**Card header (always shown):**
- A popcorn glyph 🍿 (font-size 36px) on the left.
- Title: `Lust auf frisches Popcorn?` (`card-title`, 18px).
- Subtitle, 14px, 70% opacity: `Frisch gemacht schmeckt es im Kino einfach am besten – ganz so, wie Sie es mögen.`

**Empty state (no popcorn added yet):**
- Primary accent button, full width, space-between layout (`btn btn-accent`):
  - Left: `🍿 Beliebt: Mittel · gemischt`
  - Right (font-weight 600): `+ 5,00 EUR`
  - Clicking it adds one portion preset to Mittel · gemischt.
- Ghost button (`btn btn-ghost btn-sm`): `Selbst zusammenstellen` — adds an editable portion.
- Reassurance line, 12px, 50% opacity, centered: `Kein Popcorn? Kein Problem – einfach weiter zur Zahlung.`

**Active state (≥1 portion added):** a list of portion blocks, then controls:
- Each **portion block**: `rounded-box`, background `rgba(255,255,255,.7)`, `1px solid var(--color-base-200)`, padding 12px, vertical stack gap 12px:
  - Header row (space-between): label `Portion N` (13px, 500, 70% opacity) and a remove button (`btn btn-ghost btn-xs btn-circle`, `✕`, 50% opacity).
  - **Größe** (label 12px 60% opacity) — a `join` button group, 3 equal-width buttons. Selected = `btn btn-sm btn-accent`; unselected = `btn btn-sm btn-ghost`. Each button shows the size name + a smaller 11px / 70%-opacity price line:
    - `Klein` / `3 €`
    - `Mittel` / `5 €`
    - `Groß` / `7 €`
  - **Geschmack** (label 12px 60% opacity) — radio group (`radio radio-sm radio-accent`), one name per portion: `salzig`, `süß`, `gemischt`. Default `gemischt`.
  - Price row (right-aligned, font-weight 600): the portion's line total, e.g. `5,00 EUR`.
- `+ Weitere Portion hinzufügen` — full-width `btn btn-accent btn-soft btn-sm`. Adds another portion (default Mittel · gemischt).
- **Popcorn gesamt** row (space-between): label (14px, 70% opacity) + summed popcorn total (font-weight 600).
- `Doch kein Popcorn` — small `btn btn-ghost btn-xs` (50% opacity), centered. Removes all portions and returns to the empty state.

> **Note on quantity:** there is intentionally **no per-portion quantity stepper**. Ordering
> more popcorn is done solely by adding more portions via "+ Weitere Portion hinzufügen".
> Each portion is implicitly quantity 1.

### Payment card (modified)
When popcorn has been added, the payment card shows a small breakdown above the total
(each row space-between, max-width 20rem, 14px):
- `Kinokarten (2)` · `21,00 EUR`
- `Popcorn` · `<popcorn total>`
- a 1px `var(--color-base-300)` divider
Then `Zu zahlender Betrag:` (italic) and the **grand total** (24px, bold) =
tickets (21,00 €) + popcorn. When no popcorn is added, only the plain total is shown
(`21,00 EUR`), exactly as before.

## Interactions & Behavior
- **Add suggested portion:** "Beliebt" button → append a portion {size: empfehlung (default Mittel), flavor: gemischt}.
- **Add custom portion:** "Selbst zusammenstellen" and "+ Weitere Portion hinzufügen" → append a portion {Mittel, gemischt}; user then edits it.
- **Change size:** click a size button in the `join` group → updates that portion's size; the selected button gets `btn-accent`, others `btn-ghost`; the portion line total and all totals recompute.
- **Change flavor:** select a radio → updates that portion's flavor.
- **Remove one portion:** `✕` on the portion header.
- **Remove all / opt out:** "Doch kein Popcorn" → clears all portions, card returns to the empty/nudge state.
- **Empty ⇄ active:** card shows the nudge (empty) UI when there are 0 portions, and the configurator when there are ≥1.
- **Live totals:** popcorn subtotal, payment breakdown, and grand total recompute on every change.
- No async/data-fetching; all state is local to the flow.

## State Management
Local component state for the popcorn step:
- `portions: Array<{ id: number, size: 'Klein'|'Mittel'|'Groß', flavor: 'salzig'|'süß'|'gemischt' }>` — each entry is one portion (qty always 1).
- `nextId: number` — incrementing id for stable keys across add/remove.

Derived values (computed from `portions`):
- `hasPopcorn = portions.length > 0`
- `popcornTotal = Σ price[size]` where `price = { Klein: 3, Mittel: 5, 'Groß': 7 }`
- `grandTotal = ticketTotal (21) + popcornTotal`

Two configuration props (optional, for tuning the nudge):
- `vorauswahl: boolean` (default `false`) — when true, the flow starts with one suggested portion already added (stronger nudge).
- `empfehlung: 'Klein'|'Mittel'|'Groß'` (default `'Mittel'`) — which size the "Beliebt" quick-add suggests.

Currency formatting: two decimals with a comma separator + ` EUR` (e.g. `5,00 EUR`), German locale.

## Design Tokens
All from the KinoSoft `emerald` theme — use the app's existing tokens, do not hard-code.
- **primary** (green) `oklch(76.662% .135 153.45)` — required step badges, primary CTA.
- **accent** (salmon) `oklch(72.772% .149 33.2)` — popcorn highlights only.
- **base-100** (white) `oklch(100% 0 0)` — page surface.
- **base-200** `oklch(93% 0 0)` — borders inside portion blocks, header rule.
- **base-300** `oklch(86% 0 0)` — payment breakdown divider.
- **base-content** `oklch(35.519% .032 262.988)` — body text.
- Card surface house value: `#f9fafb` (`bg-gray-50`).
- Card max-width: `28rem`; payment breakdown max-width: `20rem`.
- Card radius/shadow: daisyUI `card card-border shadow-md` (theme defaults).
- Spacing between steps: `24px` top margin on each badge; `12px` column gap.
- Type scale used: title 18px, total 24px/700, body 14px, labels 12px, price sub-line 11px.

## Screenshots
Rendered references in `screenshots/`:
- `01-popcorn-empty-state.png` — the nudge / empty state (quick-add + "Selbst zusammenstellen").
- `02-popcorn-active-state.png` — a configured portion (size `join` group, flavor radios, line total).
- `03-payment-with-popcorn.png` — the payment card with the Kinokarten + Popcorn breakdown and grand total.

## Assets
- 🍿 popcorn emoji used as a decorative glyph in the card header and the quick-add button.
  No image files. If the app prefers an icon over an emoji, substitute the codebase's icon set.

## Files
- `Kartenverkauf.dc.html` — the full flow prototype (existing steps for context + the new
  popcorn card + the modified payment card). The popcorn card markup begins at the
  `<!-- Schritt 3.5 — Popcorn (optional) -->` comment; its logic is the `Component` class at
  the bottom (`addPortion`, `update`, `remove`, `clearAll`, `renderVals`, the `prices` map,
  and `ticketTotal`).
