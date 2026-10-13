import {Component, input, output, signal} from '@angular/core';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';
import {isPresent} from '../../../common/utils';

import {Screening} from '../../models/ticket-sale';

@Component({
  selector: 'app-seat-count',
  imports: [
    ReactiveFormsModule,
    TranslatePipe,
],
  templateUrl: './seat-count.component.html',
  styleUrl: './seat-count.component.css',
})
export class SeatCountComponent {

  readonly screening = input.required<Screening>();

  readonly seatCountConfirmed = output<number>();

  readonly reset = output<void>();

  maxCount: number = 10 // TODO get from backend for screening
  countOptions: number[] = Array.from({length: this.maxCount}, (_, i) => i + 1);

  seatCountControl: FormControl<number | null> = new FormControl<number | null>(null,
    [Validators.required, Validators.min(1), Validators.max(this.maxCount)]);

  readonly done = signal(false);

  submitSeatCount() {
    if (!isPresent(this.seatCountControl.value)) {
      return;
    }
    this.done.set(true);
    this.seatCountConfirmed.emit(Number(this.seatCountControl.value));
  }

}
