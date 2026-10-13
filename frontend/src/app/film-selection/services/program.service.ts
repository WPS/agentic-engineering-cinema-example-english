import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Film} from '../models/program';

@Injectable({
  providedIn: 'root'
})
export class ProgramService {
  private http = inject(HttpClient);

  private programUrl = '/api/program';

  /** Loads the program for a day, `date` formatted as yyyy-MM-dd. */
  public getProgram(date: string): Observable<Film[]> {
    return this.http.get<Film[]>(this.programUrl, {params: {date}});
  }
}
