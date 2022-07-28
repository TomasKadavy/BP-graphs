import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayerSvgComponent } from './player-svg.component';

describe('PlayerSvgComponent', () => {
  let component: PlayerSvgComponent;
  let fixture: ComponentFixture<PlayerSvgComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlayerSvgComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerSvgComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
