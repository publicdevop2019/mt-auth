import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryPermissionComponent } from './summary-permission.component';

describe('SummaryPermissionComponent', () => {
  let component: SummaryPermissionComponent;
  let fixture: ComponentFixture<SummaryPermissionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SummaryPermissionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryPermissionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
