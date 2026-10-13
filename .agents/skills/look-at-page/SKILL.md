---
name: look-at-page
description: Look at a rendered frontend page via the e2e screenshot harness. Use when you need to see how a route renders, verify a UI/CSS change, screenshot a page, check for console/network errors, horizontal overflow, accessibility and contrast violations, layout shifts, i18n leaks, invalid HTML or what the last edit changed visually. Triggers on "look at /route", "screenshot the page", "check this view", "does this render", "iterate on a UI change".
---

# Look at a rendered page

To look at a rendered page, use the screenshot harness rather than driving a browser by hand: one command
renders the route and prints a compact report instead of a stream of browser tool calls.

For the wider decision of *which* browser surface to use (Playwright script vs. MCP vs. a browser extension
vs. raw CDP), and the security reasoning behind it, see `docs/browser-testing.md`. This skill is the
day-to-day dev loop: screenshot a route, type-check.

It needs the dev server running (`BASE_URL`, default `http://localhost:4200`; who starts it and who leaves
it alone: root `AGENTS.md`) and, once per machine, `npx playwright install chromium` inside `e2e/`. When the
dev server answers on another port, prefix every command with `BASE_URL=http://localhost:<port>`; without it
the harness shoots whatever answers on :4200, which may be another project.

KinoSoft has no login: `login.ts` is a no-op that waits for the app shell, every route is public, and a
shot without `--anon` reports `session cached` and `assumes held` as `anonymous`. There are no test users.

## Screenshot a route

```bash
npm run shot --prefix e2e -- /some/route     # --anon for public routes, --viewport=mobile, --full
```

It writes `tmp/shots/<route>.png` (repo root) and prints a compact report: the URL actually landed on (so
a silent redirect back to login shows up), whether the login it used reached the route asked for, whether
the session was fresh or cached, console errors, failed requests, horizontal overflow, and five sensor
lines (below). **Read the report first and open the PNG only when a visual judgement is actually needed.**
Overflow, contrast and what moved are numbers there, not something to squint at.

An `assumes    NOT held …` line means the app sent the shot elsewhere and the PNG shows that page, not
the route you asked for. KinoSoft has no guards and no users, so there is no other login to try: check the
route itself (a typo, a stale `screeningId`) rather than following the generic remedy the line names.

| Flag | Effect |
|---|---|
| `--anon` | skip login entirely; right for public routes such as `/`, legal pages, the landing page |
| `--viewport=mobile\|tablet\|desktop` or `=375x812` | defaults to `1280x800` |
| `--full` | full-page instead of viewport-cropped; can paint a `<video>` over the overlay that really covers it, so verify video UI with a clipped shot |
| `--wait="text=…"` | hold until a selector appears, for async views |
| `--out=name` | override the output filename |
| `--fresh` | discard the cached session and log in again |
| `--headed` | show the browser, for a human watching |
| `--port=4201` | serve from a second dev server while the browser origin stays the configured one; not needed for KinoSoft, use `BASE_URL`, see below |

### The sensor lines

After `overflow` come `a11y`, `diff`, `cls`, `text` and `html`: one head line and at most three detail
lines each. They report, they never fail the run: the exit code only says whether the shot itself worked.

| Line | Tells you |
|---|---|
| `a11y` | WCAG violations and axe best practices, worst first: contrast, missing names, `target-size`, `landmark-unique`; `--detail=a11y` adds the measured ratio and the reason per node |
| `diff` | what changed since **your previous run of the same shot** (same `--out`, viewport, `--full`, login and `--port`), not since `main`, as a pixel count, and which elements sit in the changed bands; writes `<name>.diff.png` next to the shot. A run with `diff` left out keeps the baseline where it was |
| `cls` | which elements jumped while the page loaded, invisible in a shot taken afterwards; `pushed out of view` is an element shoved below the fold, not up to the top |
| `text` | `undefined`, `NaN`, `[object Object]`, `{{ }}`, raw i18n keys (also in `aria-label`, `placeholder`, `title`, `alt`; the patterns also in form field values), and clipped text: an ellipsis, a line clamp, or an `overflow: hidden` box narrower than its text. The last is also how an icon font ligature without a glyph shows up: the ligature name renders as text and overflows the icon's box |
| `html` | invalid markup in the rendered DOM: duplicate IDs, buttons in links, invalid attribute values |

A line ending in `(wrong page)` belongs to the page a guard sent you to: fix the `assumes` line first;
`diff` skips that shot and keeps its baseline. `<label> error: …` is the sensor failing, not the page; the
others still ran. The first shot of a name says `diff  baseline created`; the second one onwards compares.

| Flag | Effect |
|---|---|
| `--checks=a11y,text` | run only these sensors, while iterating on one thing |
| `--skip=diff,html` | run all but these |
| `--detail=a11y` | up to 20 lines for that sensor (one per node, with the reason); bare `--detail` for all |
| `--scope="main"` | a11y and text on a CSS subtree only, so header and footer findings stay out |
| `--reset-diff` | forget the diff baseline and start a new one, e.g. after switching branches |

What a sensor checks, and switching one off for good, is the human's call in `e2e/shot.config.ts`; the
flags can narrow that, never widen it (`disabled in shot.config.ts`). Leave the file alone unless asked.

`E2E_USER` / `E2E_PASSWORD` would shoot as another login; KinoSoft has none, so leave them unset. The
root `AGENTS.md`, section *Test users*, says so.

**If the configured port is taken and you still need to see *your* branch** (another session or worktree
owns the dev server) start a second *frontend* dev server on another port and point `BASE_URL` at it:

```bash
# start the project's dev server a second time on :4201 (cd frontend && npm start -- --port 4201), then
BASE_URL=http://localhost:4201 npm run shot --prefix e2e -- /some/route
```

For KinoSoft `BASE_URL` **is** the right tool: there is no identity provider and no CORS list to satisfy,
and the dev server proxies `/api` to the backend, so the page's origin does not matter. `--port` is for
apps whose login pins the origin; here it would keep the browser on `http://localhost:4200`, which on some
machines belongs to another project. Only the frontend may be doubled; whether a second backend is safe
(shared database, migrations) is the project's call, root `AGENTS.md`.

## Seed local data

Seeding is not wired for KinoSoft (there is no `e2e/seed.ts` and no `npm run seed`). The data comes from
the backend's start: it recreates the database and generates the programme of the current ISO week
(`docs/Datenbank.md`). If a route renders empty, the backend is down or was started with stale data; ask
the human to (re)start it, never restart it yourself. To photograph an empty or error state without
touching data, fulfil the API route in a throwaway script with `context.route` (see `e2e/AGENTS.md`).

## After touching anything under `e2e/`

The scripts and the specs share their form steps via `e2e/flows.ts`: change a login, wizard or form
selector *there*, not in five places. Then type-check: node strips types at runtime without verifying
them, so tsc is the only thing that will tell you:

```bash
npm run typecheck --prefix e2e
```

## Scratch files

A one-off script written while looking at a page goes in `e2e/tmp/`, gitignored, and the only place from
which it can import `playwright` and `./flows.ts` (the repo root has no `node_modules`). Run it with
`cd e2e && node tmp/<name>.ts`; its PNGs go to `<repo>/tmp/shots/`, next to the harness's own. Keep it only
if the next person would run it again; then it belongs next to `shot.ts` with an entry in `e2e/package.json`.
