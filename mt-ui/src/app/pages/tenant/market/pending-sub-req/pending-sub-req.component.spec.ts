import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingSubReqComponent } from './pending-sub-req.component';

describe('PendingSubReqComponent', () => {
  let component: PendingSubReqComponent;
  let fixture: ComponentFixture<PendingSubReqComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PendingSubReqComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PendingSubReqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
