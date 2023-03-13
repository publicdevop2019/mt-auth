import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryOrgComponent } from './summary-org.component';

describe('SummaryOrgComponent', () => {
  let component: SummaryOrgComponent;
  let fixture: ComponentFixture<SummaryOrgComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SummaryOrgComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryOrgComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
