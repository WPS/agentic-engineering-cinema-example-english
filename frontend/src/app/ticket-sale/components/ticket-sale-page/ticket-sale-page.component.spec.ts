import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TicketSalePageComponent} from './ticket-sale-page.component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {TicketSaleService} from '../../services/ticket-sale.service';
import {provideRouter} from '@angular/router';
import {LanguageService} from '../../../common/i18n/language.service';

describe('TicketSalePageComponent', () => {
  let component: TicketSalePageComponent;
  let fixture: ComponentFixture<TicketSalePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        TicketSaleService
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketSalePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  afterEach(() => {
    TestBed.inject(LanguageService).setLanguage('de');
  });

  it('shows the title and the first step in the selected language', () => {
    fixture.componentRef.setInput('screeningId', '123e4567-e89b-12d3-a456-426614174000');
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;
    expect(element.textContent).toContain('Kartenverkauf');
    expect(element.textContent).toContain('Vorstellung wählen');

    TestBed.inject(LanguageService).setLanguage('en');
    fixture.detectChanges();

    expect(element.textContent).toContain('Buy tickets');
    expect(element.textContent).toContain('Choose a screening');
  });
});
