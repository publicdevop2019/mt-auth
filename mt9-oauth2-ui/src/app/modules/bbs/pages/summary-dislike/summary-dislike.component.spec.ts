import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryDislikeComponent } from './summary-dislike.component';

describe('SummaryDislikeComponent', () => {
  let component: SummaryDislikeComponent;
  let fixture: ComponentFixture<SummaryDislikeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SummaryDislikeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryDislikeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
