import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LevelSvgComponent } from './level-svg.component';

describe('LevelSvgComponent', () => {
  let component: LevelSvgComponent;
  let fixture: ComponentFixture<LevelSvgComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LevelSvgComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LevelSvgComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
