import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EndpointNewComponent } from './endpoint-new.component';

describe('EndpointNewComponent', () => {
  let component: EndpointNewComponent;
  let fixture: ComponentFixture<EndpointNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EndpointNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EndpointNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
