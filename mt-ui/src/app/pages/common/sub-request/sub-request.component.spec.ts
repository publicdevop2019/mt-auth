import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubRequestComponent } from './sub-request.component';

describe('SubRequestComponent', () => {
  let component: SubRequestComponent;
  let fixture: ComponentFixture<SubRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SubRequestComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
