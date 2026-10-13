import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ScreeningComponent} from './screening.component';
import {provideHttpClient} from '@angular/common/http';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {registerLocaleData} from '@angular/common';
import de from '@angular/common/locales/de';
import {LanguageService} from '../../../common/i18n/language.service';

describe('ScreeningComponent', () => {
  let component: ScreeningComponent;
  let fixture: ComponentFixture<ScreeningComponent>;

  const screeningId = '123e4567-e89b-12d3-a456-426614174000';

  beforeAll(() => {
    registerLocaleData(de);
  });

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ScreeningComponent
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ScreeningComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('screeningId', screeningId);
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('shows the hall name in the current language', () => {
    TestBed.inject(HttpTestingController)
      .expectOne(`/api/ticket-sale/screenings/${screeningId}`)
      .flush({id: screeningId, start: '2025-03-16T20:00:00', hall: {de: 'großer Saal', en: 'Large Hall'}, film: 'Inception'});
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;
    expect(element.textContent).toContain('großer Saal');

    TestBed.inject(LanguageService).setLanguage('en');
    fixture.detectChanges();
    expect(element.textContent).toContain('Large Hall');
  });
});
