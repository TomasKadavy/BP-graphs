import { Component, Input, OnInit, AfterViewInit } from '@angular/core';
import { INode } from 'svgson';

/**
 * Component for creating edges inside SVG wrapper
 */
@Component({
  selector: '[app-edge]',
  templateUrl: './edge.component.html',
  styleUrls: ['./edge.component.css']
})
export class EdgeComponent implements OnInit, AfterViewInit {
  @Input() element: INode = {name: "",type: "",value: "",attributes: {},children: []};

  constructor() { }

  offsetNumber: Record<string, number> = {x: 0, y: 0};
  offsetPath: string = "";
  selectedItem: any = false;

  hoverInfo() {
    // for non-reinforced graph there is no info to show on hover
    if (this.element.children.length < 5) {
      return;
    }
    return this.element.children[4].children[0]['value'];
  }

  startDrag(event: any) {
    this.selectedItem = event.target;
    this.offsetNumber = this.getMousePosition(event);

    let secondPart = this.element.children[1].attributes['d'].split("C")[1];
    let middle = this.pathPointsMove(secondPart, this.getMousePosition(event));
    this.offsetPath = this.element.children[1].attributes['d'].split("C")[0] + "C" + middle + " " + secondPart.split(" ")[secondPart.split(" ").length - 1]; 

    this.offsetNumber['x'] -= parseFloat(this.element.children[3].attributes['x']);
    this.offsetNumber['y'] -= parseFloat(this.element.children[3].attributes['y']);
  }

  endDrag(event: any) {
    this.selectedItem = null;
  }

  drag(event: any) {
    if (this.selectedItem) {
      event.preventDefault();
      let coord = this.getMousePosition(event);
      this.dragPath(coord);
      this.dragNumber(coord);
    }
  }

  pathPointsMove(oldPointsString: string,  coord: Record<string, number>): string {
    let points = oldPointsString.split(" ");
    let newPoints = []
    for (let i = 0; i < points.length - 1; i++) {
      let endPoint = points[i].split(",");
      let newX = coord['x'] - parseFloat(endPoint[0]);
      let newY = coord['y'] - parseFloat(endPoint[1]);
      newPoints.push(newX.toString() + "," + newY.toString());
    }
    return newPoints.join(" ");
  }

  dragPath(coord: Record<string, number>) {
    let start = this.offsetPath.split("C")[0];
    let middle = this.offsetPath.split("C")[1];
    let middlePoints = middle.split(" ");
    let draggedPoints = this.pathPointsMove(middle, coord);

    this.element.children[1].attributes['d'] = start + "C" + draggedPoints + " " + middlePoints[middlePoints.length - 1];
  }

  dragNumber(coord: Record<string, number>) {
    this.element.children[3].attributes['x'] = (coord['x'] - this.offsetNumber['x']).toString();
    this.element.children[3].attributes['y'] = (coord['y'] - this.offsetNumber['y']).toString();
  }

  getMousePosition(event: any) {
    let CTM = this.selectedItem.getScreenCTM();
    return {
      x: (event.clientX - CTM.e) / CTM.a,
      y: (event.clientY - CTM.f) / CTM.d
    }
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
  }


}
