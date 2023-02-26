import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorLookupComponent } from './error-lookup.component';

describe('ErrorLookupComponent', () => {
  let component: ErrorLookupComponent;
  let fixture: ComponentFixture<ErrorLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ErrorLookupComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
