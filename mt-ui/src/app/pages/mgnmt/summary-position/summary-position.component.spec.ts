import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryPositionComponent } from './summary-position.component';

describe('SummaryPositionComponent', () => {
  let component: SummaryPositionComponent;
  let fixture: ComponentFixture<SummaryPositionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SummaryPositionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryPositionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
