import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryLikeComponent } from './summary-like.component';

describe('SummaryLikeComponent', () => {
  let component: SummaryLikeComponent;
  let fixture: ComponentFixture<SummaryLikeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SummaryLikeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryLikeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
