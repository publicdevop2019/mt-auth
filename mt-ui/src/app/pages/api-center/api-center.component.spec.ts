import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiCenterComponent } from './api-center.component';

describe('ApiCenterComponent', () => {
  let component: ApiCenterComponent;
  let fixture: ComponentFixture<ApiCenterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ApiCenterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApiCenterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
