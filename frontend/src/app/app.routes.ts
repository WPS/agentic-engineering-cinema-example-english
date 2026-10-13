import {inject} from '@angular/core';
import {Router, Routes} from '@angular/router';
import {ProgramPageComponent} from './film-selection/components/program-page/program-page.component';
import {TicketSalePageComponent} from './ticket-sale/components/ticket-sale-page/ticket-sale-page.component';

export const routes: Routes = [
  {path: '', redirectTo: 'program', pathMatch: 'full'},
  {path: 'program', component: ProgramPageComponent},
  {path: 'ticket-sale/:screeningId', component: TicketSalePageComponent},
  // The German URLs of earlier versions keep working, including the old `datum` query parameter.
  {
    path: 'programm',
    redirectTo: ({queryParams}) => {
      const {datum, ...rest} = queryParams;
      return inject(Router).createUrlTree(['/program'], {queryParams: datum ? {...rest, date: datum} : rest});
    },
  },
  {path: 'kartenverkauf/:screeningId', redirectTo: 'ticket-sale/:screeningId'},
];
