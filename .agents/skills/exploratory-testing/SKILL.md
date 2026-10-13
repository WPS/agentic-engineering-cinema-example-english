---
name: exploratory-testing
description: Explore a route for defects with parallel sub-agents that only report, never fix. Use when asked to "test this page", "explore /route", "find bugs in this form", "what breaks here", "exploratory test", or before handing a flow to users. Works for any route and any of the project's test users.
---

# Exploratory testing of a page

One **dimension per sub-agent**, running in parallel against the local stack. Every agent finds and
records; **nobody fixes**. You merge the fragments into one protocol and let the human decide what
becomes work.

Splitting by dimension is what makes this worth the tokens: six agents asking six different questions
find far more than one agent asking all six, and each stays cheap because it never has to hold the
others' context. Split by dimension, never by persona: an agent playing a persona skips the tests that
persona would not think of. The user's role serves as the oracle for *Expected* (step 4), nothing more.

## 1. Pick the route and the user

The route decides the user, because a route guard will otherwise bounce the agent to some fallback page
and it will happily test that page instead. The project's test users and what each one carries are in
the root `AGENTS.md`, section *Test users*.

Log in **once** yourself before dispatching, so the agents share one cached session:

```bash
E2E_USER=<username> E2E_PASSWORD='<password>' npm run shot --prefix e2e -- <route>
```

KinoSoft has no login and no test users: run `npm run shot --prefix e2e -- <route>` without `E2E_USER`
(prefix `BASE_URL=…` if the dev server is not on the default port, see root `AGENTS.md`); the no-op login
writes `e2e/.auth/state-anonymous.json`, which is the `<slug>` below.

This writes `e2e/.auth/state-<slug>.json`. The `assumes` line in the report says whether that login
actually reached the route: if it says `NOT held`, follow the fix it names or pick another user *before*
you dispatch six agents at a wall. See the `look-at-page` skill.

Needs the dev server (and whatever backend it talks to) running; who starts them and which ones to leave
alone: root `AGENTS.md`.

## 2. Collect what is already known

The agents cost the same whether they find a new defect or the ones from last time. Before dispatching,
collect the **Known** table for the route:

- the summary table of the last protocol for the route: `ls docs/testprotokolle/`;
- the open issues that touch it: `glab issue list --search "<route>"` (origin is GitLab).

Every agent gets that table. A re-find is one line under *Confirmed* in the fragment, naming the issue
or the protocol date; a finding is only what the table does not have. The known items are the regression
test the run gives for free: an item nobody could reproduce any more is worth a line too, because it
means something got fixed on the way. A first run on a route has an empty table and says so.

## 3. Choose the dimensions

Take the ones the page actually has, usually four to six:

| Dimension | What it asks |
|---|---|
| Input limits | maxlength, numbers, dates, whitespace-only, Unicode, markup, and whether the frontend's limits match the backend's |
| Async resolution | lookups, debounces, races, what a failing endpoint does to the user |
| Dynamic elements | rows added and removed in any order, does the right one disappear, what is actually sent |
| Keyboard and a11y | whole flow without a mouse, focus after add/remove, accessible names, errors announced, contrast |
| Layout and responsive | width sweep in **filled** states; an empty form always looks fine |
| Saving and error paths | double submit, simulated 400/401/500/abort/timeout, round-trip fidelity, data loss on navigation |
| Goal completion | one realistic task end to end ("create a record with two child rows"): clicks, keystrokes, dead ends and the moments the user has to guess, measured against a budget you set **before** the run; the number is what compares releases |

**Model per agent.** If your tool lets you pick a model per sub-agent: the coordinator and the dimensions
that judge (Keyboard and a11y, Async, Saving, Goal completion) run on the strongest model. The sweeps
(Input limits, Layout) do as well on a cheaper one: they measure, they do not interpret. Expect each agent
to spend on the order of a hundred thousand tokens and ten to twenty minutes, the sweeps included.

## 4. Brief each agent

Give each one a `<slug>`, the dimension, the issue behind the route if there is one, the *Known* table,
and this block verbatim. These rules exist because each was broken once:

- **Change no product file.** No writes under the app's source directories, the docs, the protocol
  directory, `e2e/flows.ts`, `e2e/tests/`. Exactly one throwaway script at `e2e/tmp/<slug>.ts`.
- **Delete only your own file, by its exact name.** `rm -f e2e/tmp/*.ts` once destroyed five parallel
  agents' in-flight scripts.
- **Fix nothing**, open no issues, commit nothing.
- **Start and stop no servers**, and never kill the backend; the others need it. Simulate outages
  client-side with `page.route(…)`.
- **Never log out, never `--fresh`, never delete anything under `e2e/.auth/`**; the session is shared.
- **Delete no database rows.** Prefix created records with `EXPL-<slug>-` so the coordinator can clean
  up centrally; agents deleting rows in parallel delete each other's evidence.
- **The user is the oracle, the code is the map.** Before reading any code, exercise the route and write
  down what a user in that role would expect, from the UI, the issue and the project's glossary or domain
  docs if it has them. Then read the component to find the cases the UI hides and to explain causes.
  *Expected* and *Severity* measure what the user needs; the code only says why it happened.
- **A design decision counts as deliberate when it is written down**: an ADR, a domain doc, an issue or
  a code comment. Record it under *Checked without finding* with that source. A behaviour that only
  *looks* intended in the code stays a finding.

The login that costs nothing:

```ts
const browser = await chromium.launch();
const context = await browser.newContext({
  storageState: new URL('../.auth/state-<slug>.json', import.meta.url).pathname,
});
```

Run with `cd e2e && node tmp/<slug>.ts`. Read the traffic with `page.on('request')`: what is *sent* is
the standard, not what the form looks like. Screenshots go to `<repo>/tmp/shots/<slug>-*.png`, the
directory `shot.ts` writes to (`../tmp/shots/` from `e2e/`), and an agent judging appearance must **look
at them**.

**Traps.** Pass them on, so nobody pays twice. The list starts short and grows with every retro (step 7):

- The Playwright MCP shares one browser profile with every session in this repo and the first one locks
  it. Write the script straight away.
- `e2e/flows.ts` already fills the project's forms. Import it for the filled state and spend the script
  on the probe.
- `getByLabel('Name')` also hits every label that *contains* the word, a checkbox "Name visible to
  others" included: `exact: true`.
- The dev server may not proxy `/api`. Direct requests go to the backend's own port, and `/api` on the
  dev server may answer 404.

## 5. The fragment

Each agent writes to its own file in your scratchpad; one shared file would have six writers. Same
structure for all of them, so the merge is concatenation:

```markdown
## <Dimension>

**Geprüft:** <was abgedeckt wurde, wie, welcher Viewport>

### F1 — <Titel> · Schweregrad: <hoch|mittel|niedrig|kosmetisch>
- **Reproduktion:** 1. … 2. …
- **Beobachtet:** …
- **Erwartet:** …
- **Beleg:** <Messung, Request-Body, Screenshot-Pfad>
- **Ursache:** <Datei und Funktion, falls der Code gelesen wurde>

### Bestätigt (bekannt)
- <#Issue oder Protokolldatum> — <reproduziert, oder nicht mehr reproduzierbar>

### Geprüft ohne Befund
- <Fall> — <Ergebnis>

### Verdachtsmomente (unbewiesen)
- …

### Stolpersteine
- <was einen Lauf gekostet hat und was stattdessen funktioniert hat; Läufe, Tool-Aufrufe, Minuten>
```

Written in German, the language KinoSoft's humans read: the headings above are the structure, the prose is
theirs. The rest of this skill uses the English names: *Checked* = *Geprüft*, *Confirmed* = *Bestätigt*,
*Checked without finding* = *Geprüft ohne Befund*, *Suspicions* = *Verdachtsmomente*, *Snags* =
*Stolpersteine*, *Expected* = *Erwartet*, *Severity* = *Schweregrad* (`high`/`medium`/`low`/`cosmetic` =
`hoch`/`mittel`/`niedrig`/`kosmetisch`).

`high` = data loss, wrong data stored, a dead end with no way out, or the user cannot reach the goal;
`medium` blocks a secondary path or misleads; `low` costs a retry; `cosmetic` costs nothing but trust.
Severity is judged from the user's side: a dead end the code builds on purpose is still a dead end.
Agents like to downgrade a finding because the component "meant" it. Demand a reproduction the agent
triggered itself, and insist that unproven hunches go under *Suspicions*. The "checked and fine" list is
not filler: without it the protocol cannot be told apart from one where nothing was tried.

## 6. Merge, then clean up

Concatenate the fragments into one dated file where the project keeps its test protocols (step 2), each
dimension whole so every finding keeps its reproduction, and put a summary table sorted by severity on
top. Give that table an *Origin* column separating what this session introduced from what was already
there: for prioritising, that is the difference between a regression and a known weakness. The
*Confirmed* lines form a second, short table below it: which known items reproduced, which no longer do.

Then remove the `EXPL-*` records in one go, through the project's own means (an API, a SQL statement, an
admin UI), children first: do not count on cascading deletes.

KinoSoft: records cannot be removed by name. What agents create (ticket sales, booked seats) carries no
free-text field for an `EXPL-` prefix. The database is recreated on every backend start
(`docs/Datenbank.md`), so the cleanup is a backend restart: the coordinator asks the human to restart the
backend and never restarts it itself.

Findings from a session's *own* changes are regressions, not discoveries. Say so plainly rather than
burying them in the protocol.

## 7. Retro

The protocol ends with a *Tooling* section: one row per agent (runs, tool calls, tokens, minutes) and the
merged *Snags* lines. Then act on it, in this session: a trap goes into the *Traps* list in step 4 of
this skill, a harness defect (a misleading `shot` hint, a missing gitignore entry) becomes an issue. The
traps list *is* the accumulated retro; a run that ends without one has thrown away the part that makes
the next run cheaper.
