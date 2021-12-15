import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryCorsComponent } from './summary-cors.component';

describe('SummaryCorsComponent', () => {
  let component: SummaryCorsComponent;
  let fixture: ComponentFixture<SummaryCorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SummaryCorsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryCorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
