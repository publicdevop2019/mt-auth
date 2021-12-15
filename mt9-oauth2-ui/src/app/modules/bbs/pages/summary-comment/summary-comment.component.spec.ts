import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryCommentComponent } from './summary-comment.component';

describe('SummaryCommentComponent', () => {
  let component: SummaryCommentComponent;
  let fixture: ComponentFixture<SummaryCommentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SummaryCommentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryCommentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
