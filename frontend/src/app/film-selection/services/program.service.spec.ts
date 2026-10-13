import {TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';

import {ProgramService} from './program.service';
import {Film} from '../models/program';

describe('ProgramService', () => {
  let service: ProgramService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ProgramService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });
    service = TestBed.inject(ProgramService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('requests the program API with the date as query parameter and returns the films', () => {
    let films: Film[] | undefined;
    service.getProgram('2025-03-16').subscribe(result => films = result);

    const request = httpTesting.expectOne(req => req.url === '/api/program');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('date')).toBe('2025-03-16');
    expect(request.request.urlWithParams).toBe('/api/program?date=2025-03-16');

    // Wire payload as the backend sends it: content texts come bilingual.
    const payload: Film[] = [
      {
        title: 'Inception',
        runtimeMinutes: 148,
        posterUrl: 'https://example.com/posters/inception.jpg',
        ageRating: 12,
        description: {de: 'Ein Dieb, der Geheimnisse aus Träumen stiehlt.', en: 'A thief who steals secrets from dreams.'},
        genre: {de: 'Science-Fiction', en: 'Science Fiction'},
        leadActors: 'Leonardo DiCaprio',
        director: 'Christopher Nolan',
        language: {de: 'Deutsch', en: 'German'},
        screenings: [
          {id: '123e4567-e89b-12d3-a456-426614174000', start: '2025-03-16T20:00:00', hall: {de: 'großer Saal', en: 'Large Hall'}},
        ],
      },
    ];
    request.flush(payload);

    expect(films).toEqual(payload);
  });

  it('returns an empty program as an empty list', () => {
    let films: Film[] | undefined;
    service.getProgram('2025-03-17').subscribe(result => films = result);

    httpTesting.expectOne('/api/program?date=2025-03-17').flush([]);

    expect(films).toEqual([]);
  });
});
