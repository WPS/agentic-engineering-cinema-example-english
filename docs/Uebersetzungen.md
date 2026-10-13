# Übersetzungen Deutsch ↔ Englisch

Die Oberfläche ist zweisprachig (Umschalter DE/EN in der Navbar, Deutsch ist Standard, die Wahl wird im
`localStorage` unter `kinosoft.language` gemerkt). Frontend **und** Backend sind durchgängig englisch:
Bezeichner, Kommentare, REST-API, JSON und Datenbank. Deutsch bleiben nur die deutschen UI-Texte, die
deutsche Hälfte der zweisprachigen Inhalte, der Produktname *KinoSoft* und der Eigenname *FSK*.

Inhaltliche Daten (Filmbeschreibungen, Genres, Filmsprachen, Saalnamen) liefert das Backend zweisprachig als
`LocalizedText` (`{"de": "…", "en": "…"}`, Java: `de.wps.ddd.cinema.common.text.LocalizedText`); die
Oberfläche zeigt die passende Sprache über die Pipe `localized`. Filmtitel sind sprachneutral.

Inhalt: [Fachbegriffe](#fachbegriffe) · [UI-Texte](#ui-texte) · [REST-API](#rest-api) ·
[Datenbank](#datenbank) · [Pakete](#pakete-backend) · [Klassen Backend](#klassen-backend) ·
[Code-Bezeichner Frontend](#code-bezeichner-frontend)

Die UI-Tabellen werden aus den Wörterbüchern erzeugt (`frontend/src/app/common/i18n/messages/*.messages.ts`):

```bash
python3 frontend/scripts/translation-table.py
```

## Fachbegriffe

| Deutsch | Englisch (UI) | Englisch (Code) |
|---|---|---|
| Programm | Program | `program` |
| Filmauswahl | — | `filmSelection` |
| Kalender / Wochentage | Days of the week | `calendar` / `weekdays` |
| Programmeintrag | — | `programEntry` |
| Vorstellung | Screening | `screening` |
| Beginn | (Uhrzeit) | `start` |
| Saal | Hall | `hall` |
| Saalplan | Seating plan | `seatingPlan` |
| Platz | Seat | `seat` / `number` (Platznummer) |
| Reihe | Row | `row` |
| Platzanzahl / Kartenanzahl | Number of seats / tickets | `seatCount` |
| Zusammenhängende Plätze | Seats together | `adjacentSeats` |
| Kartenverkauf | Buy tickets | `ticketSale` |
| Kinokarte | Ticket | `ticket` |
| Verkaufsvorgang | — | `saleTransaction` |
| Auftragsnummer | — | `orderNumber` |
| Gesamtpreis | Amount due | `totalPrice` |
| Geldbetrag / Betrag | Amount | `moneyAmount` / `amount` |
| Währung | — | `currency` |
| Zahlung | Payment | `payment` |
| Zahlungsvorgang | — | `paymentAttempt` |
| Zahlungsstatus: Offen / Ausstehend / Eingegangen / Abgebrochen | — | `OPEN` / `PENDING` / `RECEIVED` / `CANCELLED` |
| Zahlungsmittel: Kreditkarte / Bezahlkumpel / Pear Pay | Credit card / PayBuddy / Pear Pay | `credit-card` / `pay-buddy` / `pear-pay` |
| FSK | Ages … + | `ageRating` |
| Laufzeit | Runtime (h / min) | `runtimeMinutes` |
| Regie | Director | `director` |
| Hauptdarsteller*innen | Starring | `leadActors` |
| Sprache | Language | `language` |
| Beschreibung | — | `description` |

## UI-Texte

### common

| Key | Deutsch | English |
|---|---|---|
| `common.appTitle` | KinoSoft | KinoSoft |
| `common.language.label` | Sprache | Language |
| `common.language.de` | Deutsch | German |
| `common.language.en` | Englisch | English |
| `common.menu` | Menü | Menu |

### film-selection

| Key | Deutsch | English |
|---|---|---|
| `program.title` | Programm | Program |
| `calendar.weekdays` | Wochentage | Days of the week |
| `film.poster` | Filmplakat zu {title} | Poster for {title} |
| `film.ageRating` | FSK {age} | Ages {age}+ |
| `film.director` | Regie: {name} | Director: {name} |
| `film.starring` | Hauptdarsteller*innen: {names} | Starring: {names} |
| `film.duration.hours` | {hours} Std | {hours} h |
| `film.duration.minutes` | {minutes} Min | {minutes} min |

### payment

| Key | Deutsch | English |
|---|---|---|
| `payment.title` | Zahlung | Payment |
| `payment.close` | Zahlung schließen | Close payment |
| `payment.processing` | Zahlung wird verarbeitet … | Processing payment … |
| `payment.error` | Die Zahlung konnte nicht verarbeitet werden. Bitte schließen und erneut versuchen. | The payment could not be processed. Please close this dialog and try again. |
| `payment.amountDue` | Zu zahlender Betrag | Amount due |
| `payment.methodQuestion` | Wie möchten Sie zahlen? | How would you like to pay? |
| `payment.method.creditCard` | Kreditkarte | Credit card |
| `payment.method.payBuddy` | Bezahlkumpel | PayBuddy |
| `payment.method.pearPay` | Pear Pay | Pear Pay |
| `payment.confirm` | Zahlung bestätigen | Confirm payment |

### ticket-sale

| Key | Deutsch | English |
|---|---|---|
| `ticketSale.title` | Kartenverkauf | Buy tickets |
| `ticketSale.step.screening` | Vorstellung wählen | Choose a screening |
| `ticketSale.step.seatCount` | Kartenanzahl wählen | Choose number of tickets |
| `ticketSale.step.seats` | Plätze wählen | Choose seats |
| `ticketSale.step.payment` | Kinokarten zahlen | Pay for tickets |
| `ticketSale.step.tickets` | Kinokarten drucken | Print tickets |
| `ticketSale.screening.chosen` | Sie haben folgende Vorstellung gewählt: | You have chosen the following screening: |
| `seats.count.chosen` | Sie haben folgende Platzanzahl gewählt: | Number of seats you have chosen: |
| `seats.count.question` | Wie viele Plätze benötigen Sie? | How many seats do you need? |
| `seats.count.placeholder` | bitte wählen | please choose |
| `seats.count.confirm` | Anzahl bestätigen | Confirm number |
| `seats.chosen` | Sie haben folgende Plätze gewählt: | You have chosen the following seats: |
| `seats.question` | Wo möchten Sie sitzen? | Where would you like to sit? |
| `seats.notFound` | Es konnten leider keine {count} zusammenhängenden Plätze gefunden werden. | Sorry, we could not find {count} seats together. |
| `seats.confirm` | Platzauswahl bestätigen | Confirm seats |
| `seats.single` | Reihe {row} ⋅ Platz {seat} | Row {row} ⋅ Seat {seat} |
| `seats.range` | Reihe {row} ⋅ Plätze {from} - {to} | Row {row} ⋅ Seats {from} - {to} |
| `checkout.paid` | Sie haben folgenden Betrag bezahlt: | You have paid the following amount: |
| `checkout.processing` | Zahlung wird verarbeitet … | Processing payment … |
| `checkout.amountDue` | Zu zahlender Betrag: | Amount due: |
| `checkout.resume` | Zahlung fortsetzen | Continue payment |
| `checkout.retry` | Erneut versuchen | Try again |
| `checkout.proceed` | Weiter zur Zahlung | Continue to payment |
| `tickets.congratulations` | Herzlichen Glückwunsch! Hier sind Ihre Kinokarten: | Congratulations! Here are your tickets: |
| `tickets.seat` | Reihe {row} Platz {seat} | Row {row} Seat {seat} |
| `tickets.qrCodeAlt` | QR-Code der Kinokarte | Ticket QR code |
| `tickets.print` | Kinokarten drucken | Print tickets |

## REST-API

| Alt | Neu |
|---|---|
| `GET /api/programm?datum=…` | `GET /api/program?date=…` |
| `GET /api/kartenverkauf/vorstellungen/{vorstellungId}` | `GET /api/ticket-sale/screenings/{screeningId}` |
| `GET /api/kartenverkauf/saalplaene/{vorstellungId}` | `GET /api/ticket-sale/seating-plans/{screeningId}` |
| `GET …/saalplaene/{id}/suche-zusammenhaengende-plaetze?platzanzahl=N` | `GET …/seating-plans/{id}/adjacent-seats?seatCount=N` |
| `POST /api/kartenverkauf/preisanfrage` | `POST /api/ticket-sale/price-requests` |
| `POST /api/kartenverkauf/verkaufsvorgaenge` | `POST /api/ticket-sale/sale-transactions` |
| `POST …/verkaufsvorgaenge/{auftragsnummer}/zahlungsvorgaenge` | `POST …/sale-transactions/{orderNumber}/payment-attempts` |
| `GET …/verkaufsvorgaenge/{auftragsnummer}/zahlungsstatus` | `GET …/sale-transactions/{orderNumber}/payment-status` |
| `GET …/verkaufsvorgaenge/{auftragsnummer}/kinokarten` | `GET …/sale-transactions/{orderNumber}/tickets` |
| `GET /api/zahlung/{referenz}` | `GET /api/payments/{reference}` |
| `POST /api/zahlung/{referenz}/bezahlen` | `POST /api/payments/{reference}/pay` |

### JSON-Felder

| Alt | Neu |
|---|---|
| `titel`, `laufzeit`, `fsk`, `beschreibung`, `hauptdarsteller`, `regie`, `sprache`, `vorstellungen` | `title`, `runtimeMinutes`, `ageRating`, `description`, `leadActors`, `director`, `language`, `screenings` |
| `uuid`, `beginn`, `saal` | `id`, `start`, `hall` (`{de, en}`) |
| `plaetze`, `reihe`, `platz`, `istFrei` | `seats`, `row`, `number` (Kinokarte: `seat`), `isFree` |
| `vorstellungId` | `screeningId` |
| `betrag`, `waehrung`, `gesamtpreis` | `amount`, `currency`, `totalPrice` |
| `auftragsnummer`, `anlauf` | `orderNumber`, `attempt` |
| `referenz`, `betragInCent` | `reference`, `amountInCents` |
| Status `Ausstehend` / `Eingegangen` / `Abgebrochen` / `Offen` | `PENDING` / `RECEIVED` / `CANCELLED` / `OPEN` |
| Fehlertitel „Ressource nicht gefunden“ / „Geschäftsregel verletzt“ / „Ungültige Anfrage“ | “Resource not found” / “Business rule violated” / “Invalid request” |

## Datenbank

Details und Beispielabfragen: [Datenbank.md](Datenbank.md).

| Alt | Neu |
|---|---|
| Schema `filmauswahl` | `film_selection` |
| `filmauswahl.filme` / `saele` / `vorstellungen` | `film_selection.films` / `halls` / `screenings` |
| Schema `kartenverkauf` | `ticket_sale` |
| `kartenverkauf.vorstellungen` / `saalplaene` / `plaetze` | `ticket_sale.screenings` / `seating_plans` / `seats` |
| `kartenverkauf.verkaufsvorgaenge` (+ Plätze-Tabelle) | `ticket_sale.sale_transactions` / `sale_transaction_seats` |
| `kartenverkauf.kinokarten` | `ticket_sale.tickets` |
| Spalten (z. B. `beschreibung`, `saal`, `auftragsnummer`, `zahlungsstatus`) | englisch, snake_case (`description_de`/`description_en`, `hall_de`/`hall_en`, `order_number`, `payment_status`) |

Zweisprachige Texte liegen in zwei Spalten `<feld>_de` / `<feld>_en`. Reihe und Platz heißen `row_number` /
`seat_number`, weil `ROW` in H2 ein reserviertes Wort ist.

## Pakete Backend

| Alt | Neu |
|---|---|
| `de.wps.ddd.kino` | `de.wps.ddd.cinema` |
| `…kino.wochenplanung` | `…cinema.weeklyplanning` |
| `…kino.wochenplanung.filmkatalog` | `…cinema.weeklyplanning.filmcatalog` |
| `…kino.wochenplanung.saalverwaltung` | `…cinema.weeklyplanning.hallmanagement` |
| `…kino.wochenplanung.wochenplan` | `…cinema.weeklyplanning.weeklyplan` |
| `…kino.filmauswahl` | `…cinema.filmselection` |
| `…kino.kartenverkauf` | `…cinema.ticketsale` |
| `…kino.kartenverkauf.application.domain.kartenverkauf` | `…cinema.ticketsale.application.domain.sales` |
| `…kino.kartenverkauf.application.domain.sitzplatzvergabe` | `…cinema.ticketsale.application.domain.seatallocation` |
| `…kino.kartenverkauf.application.domain.vorstellungen` | `…cinema.ticketsale.application.domain.screenings` |
| `…kino.kartenverkauf.application.domain.zahlung` | `…cinema.ticketsale.application.domain.payment` |
| `…kino.zahlung` | `…cinema.payment` |

## Klassen Backend

Tests heißen wie ihr Prüfling (`…Test`, `…IntegrationTest`). Zusätzlich neu: `LocalizedText` (common),
`WeeklyPlanQueryService` (Implementierung von `WeeklyPlanQuery`).

| Alt | Neu |
|---|---|
| `GeschaeftsregelVerletzt` | `BusinessRuleViolated` |
| `RessourceNichtGefunden` | `ResourceNotFound` |
| `AktuelleFilme` | `CurrentFilms` |
| `Saal` | `Hall` |
| `Saele` | `Halls` |
| `Vorstellung` | `Screening` |
| `Vorstellungen` | `Screenings` |
| `ProgrammService` | `ProgramService` |
| `WochenplanEventListener` | `WeeklyPlanEventListener` |
| `WochenplanImportService` | `WeeklyPlanImportService` |
| `ProgrammController` | `ProgramController` |
| `KinokartenVerkauftDto` | `TicketsSoldDto` |
| `VerkaufteKarte` | `SoldTicket` |
| `ZahlungEventListener` | `PaymentEventListener` |
| `KartenverkaufController` | `TicketSaleController` |
| `KartenDtoMapper` | `TicketDtoMapper` |
| `SaalplanDtoMapper` | `SeatingPlanDtoMapper` |
| `VorstellungDtoMapper` | `ScreeningDtoMapper` |
| `ZahlungDtoMapper` | `PaymentDtoMapper` |
| `GeldbetragDto` | `MoneyAmountDto` |
| `KartenbestellungDto` | `TicketOrderDto` |
| `KinokarteDto` | `TicketDto` |
| `PlatzDto` | `SeatDto` |
| `PlatzIdDto` | `SeatIdDto` |
| `PreisanfrageDto` | `PriceRequestDto` |
| `SaalplanDto` | `SeatingPlanDto` |
| `VerkaufsvorgangDto` | `SaleTransactionDto` |
| `VorstellungDto` | `ScreeningDto` |
| `ZahlungsstatusDto` | `PaymentStatusDto` |
| `ZahlungsvorgangDto` | `PaymentAttemptDto` |
| `ZusammenhaengendePlaetzeDto` | `AdjacentSeatsDto` |
| `KinokartenVerkauftPublisher` | `TicketsSoldPublisher` |
| `WochenplanUebernommenPublisher` | `WeeklyPlanAdoptedPublisher` |
| `ZahlungEventPublisher` | `PaymentEventPublisher` |
| `WochenplanungGateway` | `WeeklyPlanningGateway` |
| `KinokarteEntityMapper` | `TicketEntityMapper` |
| `SaalplanEntityMapper` | `SeatingPlanEntityMapper` |
| `VerkaufsvorgangEntityMapper` | `SaleTransactionEntityMapper` |
| `VorstellungEntityMapper` | `ScreeningEntityMapper` |
| `KinokarteEntity` | `TicketEntity` |
| `PlatzEntity` | `SeatEntity` |
| `SaalplanEntity` | `SeatingPlanEntity` |
| `VerkaufsvorgangEntity` | `SaleTransactionEntity` |
| `VorstellungEntity` | `ScreeningEntity` |
| `AktuelleVorstellungenImpl` | `CurrentScreeningsImpl` |
| `KinokarteRepository` | `TicketRepository` |
| `KinokartenImpl` | `TicketsImpl` |
| `SaalplanRepository` | `SeatingPlanRepository` |
| `SaalplanStapelImpl` | `SeatingPlansImpl` |
| `SaeleRepository` | `HallRepository` |
| `VerkaufsvorgaengeImpl` | `SaleTransactionsImpl` |
| `VerkaufsvorgangRepository` | `SaleTransactionRepository` |
| `VorstellungRepository` | `ScreeningRepository` |
| `Auftragsnummer` | `OrderNumber` |
| `Geldbetrag` | `MoneyAmount` |
| `KartenBlock` | `TicketBlock` |
| `Kinokarte` | `Ticket` |
| `KinokarteId` | `TicketId` |
| `KinokartenVerkauft` | `TicketsSold` |
| `Preisberechnung` | `PriceCalculation` |
| `Verkaufsvorgang` | `SaleTransaction` |
| `Verkaufsvorgangstatus` | `SaleTransactionStatus` |
| `Zahlungsstatus` | `PaymentStatus` |
| `Zahlungsvorgang` | `PaymentAttempt` |
| `ZahlungsvorgangId` | `PaymentAttemptId` |
| `Platz` | `Seat` |
| `PlatzId` | `SeatId` |
| `PlatzKategorie` | `SeatCategory` |
| `PlatzNummer` | `SeatNumber` |
| `Platzanzahl` | `SeatCount` |
| `ReiheNummer` | `RowNumber` |
| `Saalname` | `HallName` |
| `Saalplan` | `SeatingPlan` |
| `ZusammenhaengendePlaetze` | `AdjacentSeats` |
| `Beginn` | `StartTime` |
| `VorstellungId` | `ScreeningId` |
| `VorstellungKategorie` | `ScreeningCategory` |
| `WochenplanUebernommen` | `WeeklyPlanAdopted` |
| `ZahlungAbgebrochen` | `PaymentCancelled` |
| `ZahlungEingegangen` | `PaymentReceived` |
| `SaalplanBelegungFixture` | `SeatingPlanOccupancyFixture` |
| `SaeleFixture` | `HallsFixture` |
| `BerechneGesamtpreis` | `CalculateTotalPrice` |
| `HoleGewaehlteVorstellung` | `GetSelectedScreening` |
| `HoleKinokarten` | `GetTickets` |
| `HoleSaalplan` | `GetSeatingPlan` |
| `HoleZahlungsstatus` | `GetPaymentStatus` |
| `ImportiereWochenplan` | `ImportWeeklyPlan` |
| `StarteVerkaufsvorgang` | `StartSaleTransaction` |
| `StarteZahlungsvorgang` | `StartPaymentAttempt` |
| `SucheZusammenhaengendePlaetze` | `FindAdjacentSeats` |
| `VerarbeiteZahlungsabbruch` | `ProcessPaymentCancellation` |
| `VerarbeiteZahlungseingang` | `ProcessPaymentReceipt` |
| `AktuelleVorstellungen` | `CurrentScreenings` |
| `Kinokarten` | `Tickets` |
| `SaalplanStapel` | `SeatingPlans` |
| `Verkaufsmeldungen` | `SalesNotifications` |
| `Verkaufsvorgaenge` | `SaleTransactions` |
| `Wochenplaene` | `WeeklyPlans` |
| `Wochenplanmeldungen` | `WeeklyPlanNotifications` |
| `Zahlungsdienstleister` | `PaymentProvider` |
| `WochenplanDto` | `WeeklyPlanDto` |
| `WochenplanErstelltDto` | `WeeklyPlanCreatedDto` |
| `Wochenplanauskunft` | `WeeklyPlanQuery` |
| `WochenplanFixture` | `WeeklyPlanFixture` |
| `Altersfreigabe` | `AgeRating` |
| `Filmkatalog` | `FilmCatalog` |
| `Filmtitel` | `FilmTitle` |
| `Verleih` | `DistributionDeal` |
| `WochenplanRepository` | `WeeklyPlanRepository` |
| `Kalenderwoche` | `CalendarWeek` |
| `Vorstellungskategorie` | `ScreeningCategory` |
| `Vorstellungszeiten` | `ScreeningTimes` |
| `Wochenplan` | `WeeklyPlan` |
| `StarteZahlungDto` | `StartPaymentDto` |
| `ZahlungAbgebrochenDto` | `PaymentCancelledDto` |
| `ZahlungEingegangenDto` | `PaymentReceivedDto` |
| `Zahlungsabwicklung` | `PaymentProcessing` |
| `Betrag` | `Amount` |
| `Zahlung` | `Payment` |
| `Zahlungen` | `Payments` |
| `Zahlungsreferenz` | `PaymentReference` |
| `ZahlungenRepository` | `PaymentsRepository` |
| `ZahlungDto` | `PaymentDto` |
| `ZahlungsdienstleisterController` | `PaymentProviderController` |
| `ZahlungsdienstleisterDtoMapper` | `PaymentProviderDtoMapper` |
| `FesteZeitTestConfig` | `FixedClockTestConfig` |
| `IntegrationTestMitWochenplan` | `IntegrationTestWithWeeklyPlan` |

## Code-Bezeichner Frontend

Pfade relativ zu `frontend/src/app/`.

| Old path | New path | Old class (selector) | New class (selector) |
|---|---|---|---|
| `filmauswahl/` | `film-selection/` | | |
| `filmauswahl/components/programm-page/programm.component.*` | `film-selection/components/program-page/program-page.component.*` | `ProgrammComponent` (app-programm) | `ProgramPageComponent` (app-program-page) |
| `filmauswahl/components/kalender/kalender.component.*` | `film-selection/components/calendar/calendar.component.*` | `KalenderComponent` (app-kalender) | `CalendarComponent` (app-calendar) |
| `filmauswahl/components/programmeintrag/programmeintrag.component.*` | `film-selection/components/program-entry/program-entry.component.*` | `ProgrammeintragComponent` (app-programmeintrag) | `ProgramEntryComponent` (app-program-entry) |
| `filmauswahl/dtos/programm.ts` | `film-selection/models/program.ts` | `Film`, `Vorstellung` | `Film`, `Screening` |
| `filmauswahl/services/programm.service.ts` | `film-selection/services/program.service.ts` | `ProgrammService.holeProgramm` | `ProgramService.getProgram` |
| `filmauswahl/services/filmlaufzeit.pipe.ts` | `film-selection/pipes/duration.pipe.ts` | `DurationPipe` (durationFormat) | `DurationPipe` (duration) |
| `kartenverkauf/` | `ticket-sale/` | | |
| `kartenverkauf/components/kartenverkauf-page/kartenverkauf.component.*` | `ticket-sale/components/ticket-sale-page/ticket-sale-page.component.*` | `KartenverkaufComponent` (app-kartenverkauf) | `TicketSalePageComponent` (app-ticket-sale-page) |
| `kartenverkauf/components/kartenverkauf-page/badge/badge.component.*` | `ticket-sale/components/ticket-sale-page/step-badge/step-badge.component.*` | `BadgeComponent` (app-badge) | `StepBadgeComponent` (app-step-badge) |
| `kartenverkauf/components/vorstellung/vorstellung.component.*` | `ticket-sale/components/screening/screening.component.*` | `VorstellungComponent` | `ScreeningComponent` (app-screening) |
| `kartenverkauf/components/platzanzahl/platzanzahl.component.*` | `ticket-sale/components/seat-count/seat-count.component.*` | `PlatzanzahlComponent` | `SeatCountComponent` (app-seat-count) |
| `kartenverkauf/components/saalplan/saalplan.component.*` | `ticket-sale/components/seating-plan/seating-plan.component.*` | `SaalplanComponent` | `SeatingPlanComponent` (app-seating-plan) |
| `kartenverkauf/components/zahlung/zahlung.component.*` | `ticket-sale/components/checkout/checkout.component.*` | `ZahlungComponent` (app-zahlung) | `CheckoutComponent` (app-checkout) |
| `kartenverkauf/components/kinokarte/kinokarte.component.*` | `ticket-sale/components/tickets/tickets.component.*` | `KinokarteComponent` (app-kinokarte) | `TicketsComponent` (app-tickets) |
| `kartenverkauf/dtos/kartenverkauf.ts` | `ticket-sale/models/ticket-sale.ts` | German interfaces | English models (below) |
| `kartenverkauf/services/kartenverkauf.service.ts` | `ticket-sale/services/ticket-sale.service.ts` | `KartenverkaufService` | `TicketSaleService` |
| `zahlung/` | `payment/` | | |
| `zahlung/components/zahlungdialog/zahlungdialog.component.*` | `payment/components/payment-dialog/payment-dialog.component.*` | `ZahlungdialogComponent` (app-zahlungdialog) | `PaymentDialogComponent` (app-payment-dialog) |
| `zahlung/dtos/zahlung.ts` | `payment/models/payment.ts` | `Zahlung`, `Zahlungsstatus` | `Payment`, `PaymentStatus` |
| `zahlung/services/zahlung.service.ts` | `payment/services/payment.service.ts` | `ZahlungService.holeZahlung/bezahle` | `PaymentService.getPayment/pay` |
| `common/services/geldbetrag.pipe.ts` | `common/pipes/money-amount.pipe.ts` | `GeldbetragPipe` (geldbetrag) | `MoneyAmountPipe` (moneyAmount) |
| `common/services/datum.service.ts` | `common/services/date.service.ts` | `DatumService.getWochentage` | `DateService.getWeekdays` |
| `common/i18n/messages/filmauswahl.messages.ts` | `common/i18n/messages/film-selection.messages.ts` | `filmauswahlMessages` | `filmSelectionMessages` |
| `common/i18n/messages/kartenverkauf.messages.ts` | `common/i18n/messages/ticket-sale.messages.ts` | `kartenverkaufMessages` | `ticketSaleMessages` |
| `common/i18n/messages/zahlung.messages.ts` | `common/i18n/messages/payment.messages.ts` | `zahlungMessages` | `paymentMessages` |

### Routen

| Alt | Neu |
|---|---|
| `/programm?datum=…` | `/program?date=…` |
| `/kartenverkauf/:vorstellungId` | `/ticket-sale/:screeningId` |

Die alten deutschen URLs leiten weiter, auch `?datum=` wird zu `?date=`.

### Methoden (Auswahl)

| Alt | Neu |
|---|---|
| `holeProgramm` | `getProgram` |
| `holeVorstellung`, `holeSaalplan`, `holeKinokarten`, `holeZahlungsStatus` | `getScreening`, `getSeatingPlan`, `getTickets`, `getPaymentStatus` |
| `sucheZusammenhaengendePlaetze` | `findAdjacentSeats` |
| `ermittlePreis` | `getPrice` |
| `starteVerkaufsvorgang`, `starteZahlungsvorgang` | `startSaleTransaction`, `startPaymentAttempt` |
| `holeZahlung`, `bezahle` | `getPayment`, `pay` |
| `oeffneDialog`, `bestaetige`, `schliesse` | `openDialog`, `confirm`, `close` |
| `getWochentage` | `getWeekdays` |
| `waehleVorstellung`, `waehleDatum` | `selectScreening`, `selectDate` |
| Outputs `onVorstellungGeladen`, `onPlatzanzahlBestaetigt`, `onPlatzwahlBestaetigt`, `onZahlungBestaetigt`, `onKinokartenGedruckt` | `screeningLoaded`, `seatCountConfirmed`, `seatsConfirmed`, `paymentConfirmed`, `ticketsPrinted` |
| Outputs `bezahlt`, `abgebrochen` (Zahlungsdialog) | `paid`, `cancelled` |
