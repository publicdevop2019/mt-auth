import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryRoleComponent } from './summary-role.component';

describe('SummaryRoleComponent', () => {
  let component: SummaryRoleComponent;
  let fixture: ComponentFixture<SummaryRoleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SummaryRoleComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryRoleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
