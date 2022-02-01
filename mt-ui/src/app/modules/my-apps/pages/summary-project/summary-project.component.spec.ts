import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryProjectComponent } from './summary-project.component';

describe('SummaryProjectComponent', () => {
  let component: SummaryProjectComponent;
  let fixture: ComponentFixture<SummaryProjectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SummaryProjectComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryProjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
