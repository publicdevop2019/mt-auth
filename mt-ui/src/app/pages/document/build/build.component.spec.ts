import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuildComponent } from './build.component';

describe('BuildComponent', () => {
  let component: BuildComponent;
  let fixture: ComponentFixture<BuildComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BuildComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BuildComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
