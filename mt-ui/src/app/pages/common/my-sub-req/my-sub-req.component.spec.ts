import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MySubReqComponent } from './my-sub-req.component';

describe('MySubReqComponent', () => {
  let component: MySubReqComponent;
  let fixture: ComponentFixture<MySubReqComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MySubReqComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MySubReqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
