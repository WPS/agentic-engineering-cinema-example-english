# Datenbank

Das Backend nutzt [H2](https://www.h2database.com/), dateibasiert unter `./data/kinosoft.mv.db`
relativ zum Arbeitsverzeichnis, in dem der Backend-Prozess gestartet wird. Startet man das Backend
aus dem Projekt-Root (z.B. über eine IDE-Run-Konfiguration), liegt die Datei unter
`<Projektpfad>/data/kinosoft.mv.db`; bei `cd backend && ./mvnw spring-boot:run` entsprechend unter
`backend/data/`. Konfiguriert in
[`application.properties`](../backend/src/main/resources/application.properties). Der Ordner
`data/` ist per `.gitignore` ausgeschlossen.

Bei jedem Start wird das Schema neu angelegt (`spring.jpa.hibernate.ddl-auto=create`) und zur
Laufzeit aus der Wochenplanung befüllt: Die `WeeklyPlanFixture` erzeugt den Wochenplan der
**aktuellen ISO-Kalenderwoche**, Filmauswahl und Kartenverkauf übernehmen daraus ihre Sichten.
Ein Neustart des Backends genügt also, um wieder einen sauberen Datenstand zu bekommen — und
nach dem Stoppen bleibt die Datei erhalten und lässt sich in Ruhe inspizieren. (Die Tests
laufen unabhängig davon auf einer In-Memory-H2, siehe `backend/src/test/resources`.)

## Verbindung in IntelliJ

1. Backend einmal starten, damit die Datenbankdatei existiert.
2. **Database**-Tool-Window öffnen ▸ `+` ▸ **Data Source** ▸ **H2**.
3. Angaben:
   - **Connection type:** `URL only`
   - **URL:** `jdbc:h2:file:<Projektpfad>/data/kinosoft;AUTO_SERVER=TRUE`
   - **User:** `sa`, **Password:** leer
4. Fehlenden JDBC-Treiber über **Download driver files** installieren, dann **Test Connection** ▸ **OK**.

Danach sind die Schemas `film_selection` (Filmauswahl) und `ticket_sale` (Kartenverkauf) sichtbar. Dank `AUTO_SERVER=TRUE` in der
JDBC-URL darf die Verbindung auch **parallel zum laufenden Backend** geöffnet sein (H2 startet
automatisch einen kleinen Server-Prozess für die Datei) — wichtig ist nur, das `;AUTO_SERVER=TRUE`
in der URL nicht zu vergessen.

Die In-Memory-Repositories (z.B. Zahlung, Wochenplanung, Säle des Kartenverkaufs) tauchen hier bewusst nicht auf — sie
persistieren nicht in die H2.

## Alternative: H2-Konsole

Bei laufendem Backend erreichbar unter `http://localhost:8080/h2-console`, mit derselben JDBC-URL
wie in `application.properties`, Nutzer `sa` und leerem Passwort.

## Tabellen

Schema, Tabellen und Spalten sind englisch (snake_case). Zweisprachige Texte (`LocalizedText`) liegen in
zwei Spalten `<feld>_de` / `<feld>_en`. Reihe und Platz heißen `row_number` / `seat_number`, weil `ROW` in H2
ein reserviertes Wort ist.

| Schema | Tabelle | Inhalt |
|---|---|---|
| `film_selection` | `films` | Filme des Programms (`title`, `runtime_minutes`, `age_rating`, `description_de/_en`, `genre_de/_en`, `language_de/_en`, …) |
| `film_selection` | `halls` | Säle (`name_de`, `name_en`) |
| `film_selection` | `screenings` | Vorstellungen (`uuid`, `film_id`, `start`, `hall_id`) |
| `ticket_sale` | `screenings` | Vorstellungen aus Sicht des Kartenverkaufs (`film`, `start`, `hall_de/_en`, `category`) |
| `ticket_sale` | `seating_plans`, `seats` | Saalplan je Vorstellung und seine Plätze (`row_number`, `seat_number`, `category`, `sold`) |
| `ticket_sale` | `sale_transactions`, `sale_transaction_seats` | Verkaufsvorgänge (`order_number`, `status`, `payment_status`, `attempts`, `total_price_in_cents`) und ihre Plätze |
| `ticket_sale` | `tickets` | Ausgestellte Kinokarten |

## Beispielabfragen

Programm der Woche mit englischem Saalnamen:

```sql
SELECT f.title, s.start, h.name_en AS hall
FROM film_selection.screenings s
JOIN film_selection.films f ON f.id = s.film_id
JOIN film_selection.halls h ON h.id = s.hall_id
ORDER BY s.start;
```

Auslastung je Vorstellung:

```sql
SELECT sc.film, sc.start, sc.hall_en,
       COUNT(*) FILTER (WHERE se.sold) AS sold, COUNT(*) AS seats
FROM ticket_sale.screenings sc
JOIN ticket_sale.seating_plans sp ON sp.screening_id = sc.id
JOIN ticket_sale.seats se ON se.seating_plan_id = sp.id
GROUP BY sc.film, sc.start, sc.hall_en
ORDER BY sc.start;
```

Verkaufsvorgänge mit Zahlungsstand und ausgestellte Karten:

```sql
SELECT order_number, status, payment_status, attempts, total_price_in_cents
FROM ticket_sale.sale_transactions;

SELECT order_number, film_name, hall_name_en, row_number, seat_number
FROM ticket_sale.tickets
ORDER BY order_number, row_number, seat_number;
```
