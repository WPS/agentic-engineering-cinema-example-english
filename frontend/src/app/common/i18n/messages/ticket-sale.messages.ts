import {defineMessages} from './define-messages';

export const ticketSaleMessages = defineMessages(
  {
    'ticketSale.title': 'Kartenverkauf',
    'ticketSale.step.screening': 'Vorstellung wählen',
    'ticketSale.step.seatCount': 'Kartenanzahl wählen',
    'ticketSale.step.seats': 'Plätze wählen',
    'ticketSale.step.payment': 'Kinokarten zahlen',
    'ticketSale.step.tickets': 'Kinokarten drucken',

    'ticketSale.screening.chosen': 'Sie haben folgende Vorstellung gewählt:',

    'seats.count.chosen': 'Sie haben folgende Platzanzahl gewählt:',
    'seats.count.question': 'Wie viele Plätze benötigen Sie?',
    'seats.count.placeholder': 'bitte wählen',
    'seats.count.confirm': 'Anzahl bestätigen',

    'seats.chosen': 'Sie haben folgende Plätze gewählt:',
    'seats.question': 'Wo möchten Sie sitzen?',
    'seats.notFound': 'Es konnten leider keine {count} zusammenhängenden Plätze gefunden werden.',
    'seats.confirm': 'Platzauswahl bestätigen',
    'seats.single': 'Reihe {row} ⋅ Platz {seat}',
    'seats.range': 'Reihe {row} ⋅ Plätze {from} - {to}',

    'checkout.paid': 'Sie haben folgenden Betrag bezahlt:',
    'checkout.processing': 'Zahlung wird verarbeitet …',
    'checkout.amountDue': 'Zu zahlender Betrag:',
    'checkout.cancelled':
      'Die Zahlung wurde abgebrochen. Bitte versuchen Sie es erneut — gerne auch mit einem anderen Zahlungsmittel. Ihre Auswahl und der Preis bleiben bestehen.',
    'checkout.inProgress':
      'Der Zahlungsvorgang läuft noch. Ein neuer Versuch ist erst möglich, wenn dieser bezahlt oder abgebrochen wurde.',
    'checkout.resume': 'Zahlung fortsetzen',
    'checkout.retry': 'Erneut versuchen',
    'checkout.proceed': 'Weiter zur Zahlung',

    'tickets.congratulations': 'Herzlichen Glückwunsch! Hier sind Ihre Kinokarten:',
    'tickets.seat': 'Reihe {row} Platz {seat}',
    'tickets.qrCodeAlt': 'QR-Code der Kinokarte',
    'tickets.print': 'Kinokarten drucken',
  },
  {
    'ticketSale.title': 'Buy tickets',
    'ticketSale.step.screening': 'Choose a screening',
    'ticketSale.step.seatCount': 'Choose number of tickets',
    'ticketSale.step.seats': 'Choose seats',
    'ticketSale.step.payment': 'Pay for tickets',
    'ticketSale.step.tickets': 'Print tickets',

    'ticketSale.screening.chosen': 'You have chosen the following screening:',

    'seats.count.chosen': 'Number of seats you have chosen:',
    'seats.count.question': 'How many seats do you need?',
    'seats.count.placeholder': 'please choose',
    'seats.count.confirm': 'Confirm number',

    'seats.chosen': 'You have chosen the following seats:',
    'seats.question': 'Where would you like to sit?',
    'seats.notFound': 'Sorry, we could not find {count} seats together.',
    'seats.confirm': 'Confirm seats',
    'seats.single': 'Row {row} ⋅ Seat {seat}',
    'seats.range': 'Row {row} ⋅ Seats {from} - {to}',

    'checkout.paid': 'You have paid the following amount:',
    'checkout.processing': 'Processing payment …',
    'checkout.amountDue': 'Amount due:',
    'checkout.cancelled':
      'The payment was cancelled. Please try again — feel free to use a different payment method. Your selection and the price remain unchanged.',
    'checkout.inProgress':
      'The payment is still in progress. You can try again once it has been paid or cancelled.',
    'checkout.resume': 'Continue payment',
    'checkout.retry': 'Try again',
    'checkout.proceed': 'Continue to payment',

    'tickets.congratulations': 'Congratulations! Here are your tickets:',
    'tickets.seat': 'Row {row} Seat {seat}',
    'tickets.qrCodeAlt': 'Ticket QR code',
    'tickets.print': 'Print tickets',
  },
);
