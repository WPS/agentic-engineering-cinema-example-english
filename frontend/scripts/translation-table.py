#!/usr/bin/env python3
"""Renders the UI texts of the frontend as Markdown tables.

Reads the message dictionaries (src/app/common/i18n/messages/*.messages.ts), so the tables never drift
from the code. Backend content (film descriptions, genres, languages, hall names) is not listed here: it
comes bilingual from the backend as LocalizedText ({"de": ..., "en": ...}) and is no longer translated in
the frontend.

    python3 frontend/scripts/translation-table.py > docs/Uebersetzungen-UI.md
"""
import re
from pathlib import Path

I18N = Path(__file__).resolve().parent.parent / 'src/app/common/i18n'

# 'key': 'value' or "key": "value" (single-line entries, as in the message files)
ENTRY = re.compile(r"""^\s*(['"])(?P<key>(?:\\.|(?!\1).)*)\1\s*:\s*(['"])(?P<value>(?:\\.|(?!\3).)*)\3\s*,?\s*$""")


def unquote(text: str) -> str:
    return text.replace("\\'", "'").replace('\\"', '"')


def cell(text: str) -> str:
    return text.replace('|', '\\|').replace('\n', '<br>')


def message_tables() -> list[str]:
    lines = []
    for path in sorted((I18N / 'messages').glob('*.messages.ts')):
        source = path.read_text(encoding='utf-8')
        # The first object literal is German, the second English (see define-messages.ts).
        blocks = re.split(r'^\s*},\s*$', source, flags=re.M)
        de, en = ({m['key']: unquote(m['value']) for m in map(ENTRY.match, block.splitlines()) if m}
                  for block in blocks[:2])
        area = path.name.removesuffix('.messages.ts')
        lines += [f'### {area}', '', '| Key | Deutsch | English |', '|---|---|---|']
        lines += [f'| `{key}` | {cell(de[key])} | {cell(en.get(key, "—"))} |' for key in de]
        lines.append('')
    return lines


if __name__ == '__main__':
    print('\n'.join(['## UI-Texte', ''] + message_tables()))
