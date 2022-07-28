import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReinforcedSvgComponent } from './reinforced-svg.component';

describe('ReinforcedSvgComponent', () => {
  let component: ReinforcedSvgComponent;
  let fixture: ComponentFixture<ReinforcedSvgComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReinforcedSvgComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReinforcedSvgComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
