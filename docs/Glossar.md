# Glossar

Die Begriffsdefinitionen können sich je Subdomäne unterscheiden. Die Spalte *English* nennt den Begriff,
wie er im (englischen) Code und in der englischen Oberfläche heißt; weitere UI-Übersetzungen stehen in
[Uebersetzungen.md](Uebersetzungen.md).

## Filmauswahl (`filmselection`)

| Begriff | English | Definition |
|---|---|---|
| **Film :-)** | Film | Ein Kinofilm, der ins Programm aufgenommen und gezeigt werden kann. |
| **Kinoprogramm** | Program | Übersicht, welche Filme an welchen Tagen zu welchen Zeiten gezeigt werden. |
| **Saal** | Hall | Ein Vorführraum im Kino, in dem Filme gezeigt werden. |
| **Vorstellung** | Screening | Eine konkrete Aufführung eines Films zu einer bestimmten Zeit in einem bestimmten Saal. |

## Kartenverkauf (`ticketsale`)

| Begriff | English | Definition |
|---|---|---|
| **Auftragsnummer** | OrderNumber | Identifiziert einen Verkaufsvorgang; unter ihr laufen auch Zahlungsvorgänge und Kartenausgabe. |
| **Beginn** | StartTime | Datum und Uhrzeit, zu denen eine Vorstellung anfängt. |
| **Film** | Film | Der Filmtitel einer Vorstellung. Im Kartenverkauf zählt nur, *welcher* Film gezeigt wird — Laufzeit, Besetzung etc. sind hier nicht relevant. |
| **Geldbetrag** | MoneyAmount | Ein Geldwert mit Währung. |
| **Kinokarte** | Ticket | Berechtigt zum Besuch einer bestimmten Vorstellung auf einem bestimmten Platz. |
| **KinokartenVerkauft** | TicketsSold | Abschluss des Kartenverkaufs: alle Karten eines Auftrags sind beschriftet, festgehalten und übergeben. |
| **Platz** | Seat | Ein Sitzplatz im Saal, der für eine Vorstellung frei oder verkauft ist. |
| **Platzanzahl** | SeatCount | Wie viele Plätze ein Kunde für eine Bestellung wünscht. |
| **PlatzKategorie** | SeatCategory (STALLS, BOX) | Lage eines Platzes im Saal: *Parkett* (regulär) oder *Loge* (mit Aufpreis). |
| **Saal** | Hall | Der Saal, in dem eine Vorstellung stattfindet. |
| **Saalplan** | SeatingPlan | Die Sitzordnung einer Vorstellung mit dem aktuellen Belegungsstand jedes Platzes. |
| **Sitzplatzvergabe** | Seat allocation (`seatallocation`) | Das Finden, Reservieren und Verkaufen freier Plätze für eine Vorstellung. |
| **Verkaufsvorgang** | SaleTransaction | Ein Kartenverkauf: welche Plätze welcher Vorstellung ein Kinobesucher zu welchem Preis haben will. Er umfasst seinen aktuellen Zahlungsvorgang und wird mit dem Zahlungseingang abgeschlossen; ein abgelehnter Zahlungsvorgang beendet ihn nicht. |
| **Verkaufsvorgangstatus** | SaleTransactionStatus (IN_PROGRESS, COMPLETED) | Stand eines Verkaufsvorgangs: *Laufend* oder *Abgeschlossen*. Stellt sicher, dass Kinokarten nur einmal ausgestellt werden. |
| **Vorstellung** | Screening | Eine bestimmte Aufführung, für die Karten verkauft werden. |
| **VorstellungKategorie** | ScreeningCategory (STANDARD, PRIME_TIME, CHILDREN) | Preisstufe einer Vorstellung: *Standard*, *PrimeTime* oder *Kinderfilm*. |
| **Zahlungsstatus** | PaymentStatus (PENDING, RECEIVED, CANCELLED) | Stand eines Zahlungsvorgangs: *Ausstehend*, *Eingegangen* oder *Abgebrochen*. Einmal entschieden, bleibt er es. |
| **Zahlungsvorgang** | PaymentAttempt | Ein Anlauf, den Gesamtpreis zu bezahlen — Teil eines Verkaufsvorgangs, der genau einen führt und seine Anläufe zählt. Wird er abgelehnt, folgt unter derselben Auftragsnummer ein neuer, etwa mit einem anderen Zahlungsmittel; er ersetzt den gescheiterten. Erster und weiterer Anlauf sind dieselbe Operation, ein neuer erst nach dem Ende des vorigen. |
| **ZusammenhängendePlätze** | AdjacentSeats | Eine Reihe nebeneinanderliegender freier Plätze, die gemeinsam gebucht werden können. |
