import { Component, Input, OnInit } from '@angular/core';
import { INode } from 'svgson';

/**
 * Also component for SVG wrapper
 */
@Component({
  selector: '[app-graph]',
  templateUrl: './graph.component.html',
  styleUrls: ['./graph.component.css']
})
export class GraphComponent implements OnInit {
  @Input() wholeGraph: INode = {name: "",type: "",value: "",attributes: {},children: []};
  @Input() selectedPlayersList: String[] = [];
  constructor() {}

  ngOnInit(): void {
  }

}
