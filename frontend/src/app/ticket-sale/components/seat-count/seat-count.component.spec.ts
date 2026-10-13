import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeatCountComponent } from './seat-count.component';

describe('SeatCountComponent', () => {
  let component: SeatCountComponent;
  let fixture: ComponentFixture<SeatCountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SeatCountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeatCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
