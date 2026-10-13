import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SeatingPlanComponent} from './seating-plan.component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {LanguageService} from '../../../common/i18n/language.service';

describe('SeatingPlanComponent', () => {
  let component: SeatingPlanComponent;
  let fixture: ComponentFixture<SeatingPlanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        SeatingPlanComponent
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeatingPlanComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('screening', {
      id: '123e4567-e89b-12d3-a456-426614174000',
      start: '2025-03-16T20:00:00',
      hall: {de: 'großer Saal', en: 'Large Hall'},
      film: 'Inception',
    });
    fixture.componentRef.setInput('seatCount', 4);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  afterEach(() => {
    TestBed.inject(LanguageService).setLanguage('de');
  });

  it('describes a single seat in German', () => {
    component.selectedSeats.set({seats: [{row: 3, number: 5}]});

    expect(component.selectedSeatsLabel()).toBe('Reihe 3 ⋅ Platz 5');
  });

  it('describes several seats in German', () => {
    component.selectedSeats.set({seats: [5, 6, 7, 8].map(number => ({row: 3, number}))});

    expect(component.selectedSeatsLabel()).toBe('Reihe 3 ⋅ Plätze 5 - 8');
  });

  it('describes the selected seats in English once the language changes', () => {
    component.selectedSeats.set({seats: [5, 6, 7, 8].map(number => ({row: 3, number}))});
    component.done.set(true);
    fixture.detectChanges();

    TestBed.inject(LanguageService).setLanguage('en');
    fixture.detectChanges();

    const text = (fixture.nativeElement as HTMLElement).textContent;
    expect(component.selectedSeatsLabel()).toBe('Row 3 ⋅ Seats 5 - 8');
    expect(text).toContain('You have chosen the following seats:');
    expect(text).toContain('Row 3 ⋅ Seats 5 - 8');
  });

  it('describes a single seat in English', () => {
    TestBed.inject(LanguageService).setLanguage('en');
    component.selectedSeats.set({seats: [{row: 3, number: 5}]});

    expect(component.selectedSeatsLabel()).toBe('Row 3 ⋅ Seat 5');
  });
});
