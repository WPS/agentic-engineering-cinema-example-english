import {Component, computed, DestroyRef, inject, input, OnInit, output, signal} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {AdjacentSeats, Screening, Seat, SeatingPlan} from '../../models/ticket-sale';

import {TicketSaleService} from '../../services/ticket-sale.service';
import {LanguageService} from '../../../common/i18n/language.service';
import {TranslatePipe} from '../../../common/i18n/translate.pipe';

@Component({
  selector: 'app-seating-plan',
  imports: [TranslatePipe],
  templateUrl: './seating-plan.component.html',
  styleUrl: './seating-plan.component.css'
})
export class SeatingPlanComponent implements OnInit {
  private ticketSaleService = inject(TicketSaleService);
  private destroyRef = inject(DestroyRef);
  private languageService = inject(LanguageService);

  readonly screening = input.required<Screening>();

  readonly seatCount = input.required<number>();

  readonly seatsConfirmed = output<AdjacentSeats>();

  readonly seatingPlan = signal<SeatingPlan | undefined>(undefined);
  readonly offeredSeats = signal<AdjacentSeats | undefined>(undefined);
  readonly selectedSeats = signal<AdjacentSeats | undefined>(undefined);
  readonly hoveredSeats = signal<AdjacentSeats | undefined>(undefined);
  readonly done = signal(false);

  radius = 20;
  spacing = 53;
  rowSpacing = 80;
  startY = 70;

  readonly seatsLoaded = computed(() => this.offeredSeats() !== undefined);

  readonly seatsFound = computed(
    () => this.offeredSeats()?.seats.length === this.seatCount()
  );

  readonly seatingPlanLoaded = computed(
    () => this.seatingPlan() !== undefined && this.offeredSeats() !== undefined
  );

  readonly seatingPlanWidth = computed(() => {
    const plan = this.seatingPlan();
    if (!plan) return 0;
    const maxLength = Math.max(...plan.seats.map(row => row.length));
    return this.spacing * maxLength + this.radius * 2;
  });

  readonly seatingPlanHeight = computed(() => {
    const plan = this.seatingPlan();
    if (!plan) return 0;
    return this.startY + plan.seats.length * this.rowSpacing;
  });

  readonly screenWidth = computed(() => this.seatingPlanWidth() - ((this.seatingPlanWidth() / 8) * 2));

  readonly screenPosition = computed(() => this.seatingPlanWidth() / 8);

  readonly seatRows = computed<Seat[][]>(() => this.seatingPlan()?.seats ?? []);

  readonly selectedSeatsLabel = computed(() => {
    const selected = this.selectedSeats();
    if (!selected || selected.seats.length === 0) return '';
    if (selected.seats.length === 1) {
      return this.languageService.translate('seats.single', {
        row: selected.seats[0].row,
        seat: selected.seats[0].number,
      });
    }
    return this.languageService.translate('seats.range', {
      row: selected.seats[0].row,
      from: selected.seats[0].number,
      to: selected.seats.at(-1)!.number,
    });
  });

  ngOnInit(): void {
    this.ticketSaleService.getSeatingPlan(this.screening().id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((data: SeatingPlan) => this.seatingPlan.set(data));

    this.ticketSaleService.findAdjacentSeats(this.screening().id, this.seatCount())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((data: AdjacentSeats) => {
        this.offeredSeats.set(data);
        this.selectedSeats.set(data);
      });
  }

  confirmSeats() {
    this.done.set(true);
    this.seatsConfirmed.emit(this.selectedSeats()!);
  }

  getStartX(row: Seat[]): number {
    const rowWidth = (row.length) * this.spacing;
    return (this.seatingPlanWidth() - rowWidth);
  }

  getSeatColor(seat: Seat): string {
    if (this.hoveredSeats()?.seats.some(s => s.number === seat.number && s.row === seat.row)) {
      return 'yellow';
    }

    if (this.selectedSeats()?.seats.some(s => s.number === seat.number && s.row === seat.row)) {
      return 'lightgreen';
    }

    if (seat.isFree) {
      return 'white';
    }

    return 'lightgray';
  }

  seatEnter(rowIndex: number, columnIndex: number) {
    const block = this.findFreeBlock(rowIndex, columnIndex);
    if (block) {
      this.hoveredSeats.set(block);
    }
  }

  seatLeave() {
    this.hoveredSeats.set(undefined);
  }

  seatClick(rowIndex: number, columnIndex: number) {
    const block = this.findFreeBlock(rowIndex, columnIndex);
    if (block) {
      this.selectedSeats.set(block);
    }
  }

  private findFreeBlock(rowIndex: number, columnIndex: number): AdjacentSeats | undefined {
    let row = this.seatingPlan()!.seats[rowIndex];

    if (!row[columnIndex].isFree) return undefined;

    let li = columnIndex;
    let ri = columnIndex;
    let n = 1;
    let steptaken = true;

    const seatCount = this.seatCount();
    while (steptaken) {
      steptaken = false;
      if (n == seatCount) break;
      if (ri < row.length - 1 && row[ri + 1].isFree) {
        ri++;
        n++;
        steptaken = true;
      }
      if (n == seatCount) break;
      if (li > 0 && row[li - 1].isFree) {
        li--;
        n++;
        steptaken = true;
      }
    }

    if (n == seatCount) {
      return {seats: row.slice(li, ri + 1).map(seat => ({row: seat.row, number: seat.number}))};
    }

    return undefined;
  }
}
