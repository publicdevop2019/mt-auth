import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CorsComponent } from './cors.component';

describe('CorsComponent', () => {
  let component: CorsComponent;
  let fixture: ComponentFixture<CorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CorsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
