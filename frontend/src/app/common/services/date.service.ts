import {Injectable} from '@angular/core';
import {addDays, startOfWeek} from 'date-fns';

@Injectable({
  providedIn: 'root'
})
export class DateService {

  /** Returns the seven days (Monday to Sunday) of the week containing `date`, each at midnight. */
  getWeekdays(date: Date): Date[] {
    const monday = startOfWeek(date, {weekStartsOn: 1});
    return Array.from({length: 7}, (_, i) => addDays(monday, i));
  }
}
