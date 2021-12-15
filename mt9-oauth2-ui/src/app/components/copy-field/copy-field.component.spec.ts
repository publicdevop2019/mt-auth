import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CopyFieldComponent } from './copy-field.component';

describe('CopyFieldComponent', () => {
  let component: CopyFieldComponent;
  let fixture: ComponentFixture<CopyFieldComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CopyFieldComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
