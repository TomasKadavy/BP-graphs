import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { INode } from 'svgson';

/**
 * Component for creating nodes inside SVG wrapper
 */
@Component({
  selector: '[app-node]',
  templateUrl: './node.component.html',
  styleUrls: ['./node.component.css']
})
export class NodeComponent implements OnInit, AfterViewInit {
  @Input() element: INode = {name: "",type: "",value: "",attributes: {},children: []};
  @Input() wholeGraph: INode = {name: "",type: "",value: "",attributes: {},children: []};
  @Input() selectedPlayersList: String[] = [];

  startingEdges: INode[] = [];
  startingEdgesCopyOffset: INode[] = [];
  endingEdges: INode[] = [];
  endingEdgesCopyOffset: INode[] = [];

  selectedElement: any = false;
  offsetText: Record<string, number> = {x: 0, y: 0};

  offset: Record<string, number> = {x: 0, y: 0};
  startDragPointsPosition: string[][] = [[]];
  
  constructor() { }

  hoverInfo() {
    // for non-reinforced graph there is no info to show on hover
    if (this.element.children.length < 5) {
      return;
    }
    // only display arguments for selected player on the nodes and display correctly for level graph
    if (this.selectedPlayersList.length !== 0 && this.selectedPlayersList[0] != "-1") {
      let commandsPlayer = this.element.children[4].children[0]['value'].substring(1).split("|");
      let intersection: String[] = [];
      for (let i = 0; i < commandsPlayer.length - 1; i++) {
        if (this.contains(this.selectedPlayersList, commandsPlayer[i].split(",")[0])) {
          intersection.push(commandsPlayer[i]);
        } 
      }

      if (intersection.length === 0) {
        return "Selected player(s) did not go via this node."
      }
      let result = "";
      for (let i = 0; i < intersection.length; i++) {
        let info = intersection[i].split(",");
        result += info[0] + "-id sandbox:\n";
        for (let j = 1; j < info.length - 1; j++) {
          result += "\t" + j.toString() + ": " + info[j] + "\n";
        }
      }
      return result;
    }
    // if no player/players are selected
    let result = "";
    let info = this.element.children[4].children[0]['value'].substring(1);
    let players = info.split("|");
    for (let i = 0; i < players.length - 1; i++) {
      let commandsPlayer = players[i].split(",");
      result += commandsPlayer[0] + "-id sandbox:\n";
      for (let j = 1; j < commandsPlayer.length - 1; j++) {
        result += "\t" + j.toString() + ": " + commandsPlayer[j] + "\n";
      }
    }
    return result;
  }

  contains(a: String[], obj: String) {
    var i = a.length;
    while (i--) {
       if (a[i] == obj) {
           return true;
       }
    }
    return false;
}

  dragOutgoingEdges(coord: Record<string, number>) {
    for (let i = 0; i < this.startingEdges.length; i++) {
      // drag numbers in edges
      this.startingEdges[i].children[3].attributes['x'] = (coord['x'] - parseFloat(this.startingEdgesCopyOffset[i].children[0].attributes['x'])).toString();
      this.startingEdges[i].children[3].attributes['y'] = (coord['y'] - parseFloat(this.startingEdgesCopyOffset[i].children[0].attributes['y'])).toString();
      // drag start of path in edges
      this.startingEdges[i].children[1].attributes['d'] = "M" + this.pathPointsMove(this.startingEdgesCopyOffset[i].children[2].attributes['d'].split("C")[0].slice(1), coord) +
      "C" + this.pathPointsMove(this.startingEdgesCopyOffset[i].children[2].attributes['d'].split("C")[1].split(" ").slice(0, -1).join(" "), coord) +
      " " + this.startingEdges[i].children[1].attributes['d'].split("C")[1].split(" ").slice(-1)[0];
    }
  }

  dragIncomingEdges(coord: Record<string, number>) {
    for (let i = 0; i < this.endingEdges.length; i++) {
      // drag numbers in edges
      this.endingEdges[i].children[3].attributes['x'] = (coord['x'] - parseFloat(this.endingEdgesCopyOffset[i].children[0].attributes['x'])).toString();
      this.endingEdges[i].children[3].attributes['y'] = (coord['y'] - parseFloat(this.endingEdgesCopyOffset[i].children[0].attributes['y'])).toString();
      // drag triangles in edges
      this.endingEdges[i].children[2].attributes['points'] = this.trianglePointsMove(this.endingEdgesCopyOffset[i].children[1].attributes['points'], coord);
      // drag end of path in edges
      this.endingEdges[i].children[1].attributes['d'] = this.endingEdges[i].children[1].attributes['d'].split("C")[0] +
       "C" + this.pathPointsMove(this.endingEdgesCopyOffset[i].children[2].attributes['d'].split("C")[1], coord);
    }
  }

  edgesCopyOffest(copyList: INode[], source: INode[], coord: Record<string, number>) {
    source.forEach(edge => {
      // text of edge
      let newEdge: INode = {name: "",type: "",value: "",attributes: {},children: []};
      let newText: Record<string, string> = {x: (coord['x'] - parseFloat(edge.children[3].attributes['x'])).toString(),
       y: (coord['y'] - parseFloat(edge.children[3].attributes['y'])).toString()};
      let newTextChild: INode = {name: "", type: "", value: "", attributes: newText, children: []};
      newEdge.children.push(newTextChild);

      // triangle of edge
      let trianglePointsMove = this.trianglePointsMove(edge.children[2].attributes['points'], coord);
      let newPolygonPoints: Record<string, string> =  {points: trianglePointsMove};
      let newPolygonChild: INode = {name: "", type: "", value: "", attributes: newPolygonPoints, children: []};
      newEdge.children.push(newPolygonChild);

      // path of edge
      let newPathPoints: string = "M" + this.pathPointsMove(edge.children[1].attributes['d'].split("C")[0].slice(1), coord) + "C" + this.pathPointsMove(edge.children[1].attributes['d'].split("C")[1], coord);
      let newPathPointsChild: INode = {name: "", type: "", value: "", attributes: {d: newPathPoints}, children: []};
      newEdge.children.push(newPathPointsChild);

      copyList.push(newEdge);
    });
  }

  pathPointsMove(oldPointsString: string,  coord: Record<string, number>): string {
    let points = oldPointsString.split(" ");
    let newPoints = []
    for (let i = 0; i < points.length; i++) {
      let endPoint = points[i].split(",")
      let newX = coord['x'] - parseFloat(endPoint[0]);
      let newY = coord['y'] - parseFloat(endPoint[1]);
      newPoints.push(newX.toString() + "," + newY.toString());
    }
    return newPoints.join(" ");
  }

  trianglePointsMove(oldPointsString: string, coord: Record<string, number>): string {
    let oldPoints = this.getPolygonPoints(oldPointsString);
    let newTrianglePoints = []
    for (let i = 0; i < oldPoints.length; i++) {
      let newX = coord['x'] - (this.offset['x'] - parseFloat(oldPoints[i][0]));
      let newY = coord['y'] - (this.offset['y'] - parseFloat(oldPoints[i][1]));
      let onePoint = newX.toString() + "," + newY.toString();
      newTrianglePoints.push(onePoint); 
    }
    let result = newTrianglePoints[0];
    for (let i = 1; i < newTrianglePoints.length; i++) {
      result += " " + newTrianglePoints[i];
    }
    return result;
  }

  startDrag(event: any) {
    this.selectedElement = event.target;
    this.offsetText = this.getMousePosition(event);
    this.offset = this.getMousePosition(event);

    this.startingEdgesCopyOffset = [];
    this.endingEdgesCopyOffset = [];
    this.edgesCopyOffest(this.startingEdgesCopyOffset, this.startingEdges, this.getMousePosition(event));
    this.edgesCopyOffest(this.endingEdgesCopyOffset, this.endingEdges, this.getMousePosition(event));

    this.offsetText['x'] -= parseFloat(this.selectedElement.getAttributeNS(null, "x"));
    this.offsetText['y'] -= parseFloat(this.selectedElement.getAttributeNS(null, "y"));
    // If Polygon is selected and not the text by mouse
    if (isNaN(this.offsetText['x'])) {
      this.offsetText = this.getMousePosition(event);
      this.offsetText['x'] -= parseFloat(this.element.children[2].attributes['x']);
      this.offsetText['y'] -= parseFloat(this.element.children[2].attributes['y']);
    }
    this.startDragPointsPosition = this.getPolygonPoints(this.element.children[1].attributes['points']);
  }

  getPolygonPoints(points: string) {
    let newPoints = []
    let onePoints = points.split(" ");
    for (let i = 0; i < onePoints.length; i++) {
      newPoints.push(onePoints[i].split(","));
    }
    return newPoints;
  }

  getMousePosition(event: any) {
    let CTM = this.selectedElement.getScreenCTM();
    return {
      x: (event.clientX - CTM.e) / CTM.a,
      y: (event.clientY - CTM.f) / CTM.d
    }
  }

  dragPolygon(coord: Record<string, number>) {
    let newPolygonPoints = [];
    let polygonPoints = this.getPolygonPoints(this.element.children[1].attributes['points']);
    for (let i = 0; i < polygonPoints.length; i++) {
      let newX = coord['x'] - (this.offset['x'] - parseFloat(this.startDragPointsPosition[i][0]));
      let newY = coord['y'] - (this.offset['y'] - parseFloat(this.startDragPointsPosition[i][1]));
      let onePoint = newX.toString() + "," + newY.toString();
      newPolygonPoints.push(onePoint); 
    }

    let result = newPolygonPoints[0];
    for (let i = 1; i < newPolygonPoints.length; i++) {
      result += " " + newPolygonPoints[i];
    }
    this.element.children[1].attributes['points'] = result;
  }

  dragText(coord: Record<string, number>) {
    this.element.children[2].attributes['x'] = (coord['x'] - this.offsetText['x']).toString();
    this.element.children[2].attributes['y'] = (coord['y'] - this.offsetText['y']).toString();
  }

  drag(event: any) {
    if (this.selectedElement) {
      event.preventDefault();
      let coord = this.getMousePosition(event);
      this.dragText(coord);
      this.dragPolygon(coord);
      this.dragOutgoingEdges(coord);
      this.dragIncomingEdges(coord);
    }
  }

  endDrag(event: any) {
    if (event.type == 'mouseup') {
      this.selectedElement = null;
    }

  }

  getStartingEdges(): INode[] {
    let allEdges: INode[] = [];
    this.wholeGraph.children.forEach(child => {
      if (child.attributes['class'] == 'edge') {
        if (this.getThisNodeTitle() === this.getEdgeStartingNode(child))
        allEdges.push(child);
      }
    });
    
    return allEdges;
  }

  getEdgeStartingNode(edge: INode): string {
    let title: string = edge.children[0].children[0]['value'];
    let titleSplited = title.split(/&#45;/);
    if (titleSplited[0].length == 0) {
      return titleSplited[1];
    }
    return titleSplited[0];
  }

  getThisNodeTitle(): string {
    let title = this.element.children[0].children[0].value;
    if (title.split(";").length == 2) {
      return title.split(";")[1];
    }
    return title;
  }

  getEndingEdges(): INode[] {
    let allEdges: INode[] = [];
    this.wholeGraph.children.forEach(child => {
      if (child.attributes['class'] == 'edge') {
        if (this.getThisNodeTitle() === this.getEdgeEndingNode(child))
        allEdges.push(child);
      }
    });
    return allEdges;
  }

  getEdgeEndingNode(edge: INode): string {
    let title: string = edge.children[0].children[0]['value'];
    let titleSplited = title.split(/&#45;/);
    if (titleSplited.length == 4) {
      return titleSplited[3];
    }
    if (titleSplited[0].length == 0) {
      return titleSplited[2].split(";")[titleSplited[2].split(";").length - 1];
    }
    if(titleSplited.length == 2) {
      return titleSplited[1].split(";")[1];
    }
    return titleSplited[2];
  }

  ngAfterViewInit(): void {
    this.startingEdges = this.getStartingEdges();
    this.endingEdges = this.getEndingEdges();
  }

  ngOnInit(): void {
  }
}
