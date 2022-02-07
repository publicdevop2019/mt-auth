import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyRolesComponent } from './my-roles.component';

describe('MyRolesComponent', () => {
  let component: MyRolesComponent;
  let fixture: ComponentFixture<MyRolesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyRolesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyRolesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
