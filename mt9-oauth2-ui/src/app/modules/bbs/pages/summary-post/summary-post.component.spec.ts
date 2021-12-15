import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryPostComponent } from './summary-post.component';

describe('SummaryPostComponent', () => {
  let component: SummaryPostComponent;
  let fixture: ComponentFixture<SummaryPostComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SummaryPostComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryPostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
