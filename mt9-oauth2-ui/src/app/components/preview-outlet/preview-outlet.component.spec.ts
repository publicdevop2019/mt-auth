import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewOutletComponent } from './preview-outlet.component';

describe('PreviewOutletComponent', () => {
  let component: PreviewOutletComponent;
  let fixture: ComponentFixture<PreviewOutletComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PreviewOutletComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PreviewOutletComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
