import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogGraphComponent } from './dialog-graph.component';

describe('DialogGraphComponent', () => {
  let component: DialogGraphComponent;
  let fixture: ComponentFixture<DialogGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogGraphComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
