import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LunchComponent } from './lunch.component';

describe('LunchComponent', () => {
  let component: LunchComponent;
  let fixture: ComponentFixture<LunchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LunchComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LunchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
