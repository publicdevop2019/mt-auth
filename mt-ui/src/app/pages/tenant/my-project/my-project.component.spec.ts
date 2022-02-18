import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyProjectComponent } from './my-project.component';

describe('MyProjectComponent', () => {
  let component: MyProjectComponent;
  let fixture: ComponentFixture<MyProjectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyProjectComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyProjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
