import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ProgramEntryComponent} from './program-entry.component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {LanguageService} from '../../../common/i18n/language.service';
import {Film} from '../../models/program';

describe('ProgramEntryComponent', () => {
  let component: ProgramEntryComponent;
  let fixture: ComponentFixture<ProgramEntryComponent>;

  beforeEach(async () => {
    localStorage.clear();
    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ProgramEntryComponent
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramEntryComponent);
    component = fixture.componentInstance;
    // Program entry as delivered by the program service. Content values (description, genre, language,
    // hall) come bilingual from the backend; the localized pipe picks the current language.
    const film: Film = {
      title: 'Inception',
      runtimeMinutes: 148,
      posterUrl: 'https://example.com/posters/inception.jpg',
      ageRating: 12,
      description: {
        de: 'Ein Dieb, der Geheimnisse aus den Träumen anderer stiehlt, erhält die Chance, seine Vergangenheit auszulöschen.',
        en: 'A thief who steals secrets from the dreams of others gets the chance to erase his past.',
      },
      genre: {de: 'Science-Fiction', en: 'Science Fiction'},
      leadActors: 'Leonardo DiCaprio',
      director: 'Christopher Nolan',
      language: {de: 'Deutsch', en: 'German'},
      screenings: [
        {
          id: '123e4567-e89b-12d3-a456-426614174000',
          start: '2025-03-16T20:00:00',
          hall: {de: 'kleiner Saal', en: 'Small Hall'},
        },
        {
          id: '123e4567-e89b-12d3-a456-426614174001',
          start: '2025-03-17T18:30:00',
          hall: {de: 'großer Saal', en: 'Large Hall'},
        },
      ],
    };
    fixture.componentRef.setInput('film', film);
    fixture.detectChanges();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('renders the film details in German by default', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent;

    expect(text).toContain('FSK 12');
    expect(text).toContain('2 Std 28 Min');
    expect(text).toContain('Regie: Christopher Nolan');
    expect(text).toContain('Hauptdarsteller*innen: Leonardo DiCaprio');
    expect(text).toContain('20:00 Uhr');
    expect(text).toContain('großer Saal');
    expect(text).toContain('Deutsch');
    expect(text).toContain('Ein Dieb, der Geheimnisse');
    expect(text).not.toContain('Large Hall');
  });

  it('renders the film details in English after a language switch', () => {
    TestBed.inject(LanguageService).setLanguage('en');
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;
    const text = element.textContent;

    expect(text).toContain('Ages 12+');
    expect(text).toContain('2 h 28 min');
    expect(text).toContain('Director: Christopher Nolan');
    expect(text).toContain('Starring: Leonardo DiCaprio');
    expect(text).toContain('German');
    expect(text).toContain('8:00 PM');
    expect(text).toContain('Large Hall');
    expect(text).toContain('Small Hall');
    expect(text).toContain('Science Fiction');
    expect(text).toContain('A thief who steals secrets');
    expect(text).not.toContain('großer Saal');
    expect(element.querySelector('img')?.getAttribute('alt')).toBe('Poster for Inception');
  });
});
