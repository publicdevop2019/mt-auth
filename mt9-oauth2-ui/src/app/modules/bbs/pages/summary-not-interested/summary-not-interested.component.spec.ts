import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryNotInterestedComponent } from './summary-not-interested.component';

describe('SummaryNotInterestedComponent', () => {
  let component: SummaryNotInterestedComponent;
  let fixture: ComponentFixture<SummaryNotInterestedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SummaryNotInterestedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryNotInterestedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
