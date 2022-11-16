import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubReqRejectDialogComponent } from './sub-req-reject-dialog.component';

describe('SubReqRejectDialogComponent', () => {
  let component: SubReqRejectDialogComponent;
  let fixture: ComponentFixture<SubReqRejectDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubReqRejectDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubReqRejectDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
