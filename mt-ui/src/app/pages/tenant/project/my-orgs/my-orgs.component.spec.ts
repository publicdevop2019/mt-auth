import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyOrgsComponent } from './my-orgs.component';

describe('MyOrgsComponent', () => {
  let component: MyOrgsComponent;
  let fixture: ComponentFixture<MyOrgsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyOrgsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyOrgsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
