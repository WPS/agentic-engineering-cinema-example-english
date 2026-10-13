import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ProgramPageComponent} from './program-page.component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('ProgramPageComponent', () => {
  let component: ProgramPageComponent;
  let fixture: ComponentFixture<ProgramPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        ProgramPageComponent
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProgramPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
