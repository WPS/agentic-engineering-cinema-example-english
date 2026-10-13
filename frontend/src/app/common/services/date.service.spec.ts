import {TestBed} from '@angular/core/testing';

import {DateService} from './date.service';

describe('DateService', () => {
  let service: DateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DateService]
    });
    service = TestBed.inject(DateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getWeekdays', () => {
    it('should return week of days beginning monday', () => {
      const date = new Date("2025-03-18T16:30:00");
      const expectedResult = [
        new Date("2025-03-17T00:00:00"),
        new Date("2025-03-18T00:00:00"),
        new Date("2025-03-19T00:00:00"),
        new Date("2025-03-20T00:00:00"),
        new Date("2025-03-21T00:00:00"),
        new Date("2025-03-22T00:00:00"),
        new Date("2025-03-23T00:00:00")
      ]

      const result = service.getWeekdays(date);

      expect(result).toEqual(expectedResult);
    });
  })

});
