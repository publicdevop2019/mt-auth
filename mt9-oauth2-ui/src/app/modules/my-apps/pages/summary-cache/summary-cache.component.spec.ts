import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SummaryCacheComponent } from './summary-cache.component';

describe('SummaryCacheComponent', () => {
  let component: SummaryCacheComponent;
  let fixture: ComponentFixture<SummaryCacheComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SummaryCacheComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryCacheComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
