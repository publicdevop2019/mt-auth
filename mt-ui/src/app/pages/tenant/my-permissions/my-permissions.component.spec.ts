import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyPermissionsComponent } from './my-permissions.component';

describe('MyPermissionsComponent', () => {
  let component: MyPermissionsComponent;
  let fixture: ComponentFixture<MyPermissionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyPermissionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyPermissionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
