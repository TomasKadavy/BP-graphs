import { Component } from '@angular/core';
import { RestService } from '../rest.service';
import { AbstractGraph } from '../abstract-graph';

/**
 * Component for the SVG wrapper 
 */
@Component({
  selector: 'app-svg',
  templateUrl: './svg.component.html',
  styleUrls: ['./svg.component.css']
})
export class SvgComponent extends AbstractGraph {
  constructor(restService: RestService) {
    super(restService, 'normal');
  }
}