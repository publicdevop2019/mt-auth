import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyPositionsComponent } from './my-positions.component';

describe('MyPositionsComponent', () => {
  let component: MyPositionsComponent;
  let fixture: ComponentFixture<MyPositionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyPositionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyPositionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
