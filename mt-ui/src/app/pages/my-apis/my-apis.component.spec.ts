import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyApisComponent } from './my-apis.component';

describe('MyApisComponent', () => {
  let component: MyApisComponent;
  let fixture: ComponentFixture<MyApisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyApisComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyApisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
