import { Injectable } from '@angular/core';
import { INode, parse } from 'svgson';
import { RestService } from './rest.service';
import { firstValueFrom } from 'rxjs';
import { MatListOption } from '@angular/material/list';

/**
 * Abstract class including functionality for all types of graphs
 */
@Injectable()
export abstract class AbstractGraph {
  svg: INode = { name: "empty", type: "svg", children: [{ name: "first", type: "element", children: [], value: "none", attributes: {transform: "scale(1 1) rotate(0) translate(4 736)"} }], value: "none", attributes: {} };
  attributes: Record<string, string> = {}

  wrapAttributes: any = { width: "100%", height: "100%" };

  copySVG: string = "";
  copyTransform: string = "";
  copyViewBox: string = "";

  offset: Record<string, number> = {x: 0, y: 0};
  isPointerDown = false;

  isControlKeyDown = false;

  inputFile: any;

  players: Number[] = [];
  lastSelectedPlayer: Number = -1;
  
  selectedPlayersList: String[] = [];

  constructor(public restService: RestService, private typeGraph: String) {
    this.loadGraph();
  }

  async loadGraph() {
    try {
      if (this.typeGraph === 'normal') {
        this.inputFile = await firstValueFrom(this.restService.getGraph());
      } else if (this.typeGraph === 'reinforced') {
        this.inputFile = await firstValueFrom(this.restService.getReinforcedGraph());
      } else {
        this.inputFile = await firstValueFrom(this.restService.getIndividualPlayerGraph(-1))
      }
      if (this.inputFile.size === 0) {
        this.delay(1000).then(() => this.loadGraph());
      }
      let fileData = await this.readInputFile(this.inputFile) as string;
      parse(fileData).then((json) => this.setSvg(json));
      this.copySVG = fileData;
    } catch (error) {
      console.log(error);
    }
  }

  setSvg(value: INode) {
    console.log(value);
    if (value !== this.svg) {
        this.svg = value;
        this.attributes = value.attributes;
        this.copyTransform = this.svg.children[0].attributes['transform'];
        this.copyViewBox = this.svg.attributes['viewBox'];
    }
}

  readInputFile(file: File) {
    return new Promise((resolve, reject) => {
      let fileReader = new FileReader();
      fileReader.onload = () => {
        resolve(fileReader.result);
      }
      fileReader.readAsText(file);
    })
  }

  startDrag(event: any) {
    if (event.target.classList['value'] !== 'svgWrap') {
      return;
    }
    if (this.svg.name === 'empty') {
      return;
    }
    this.offset['x'] = event.clientX + parseFloat(this.svg.attributes['viewBox'].split(" ")[0]);
    this.offset['y'] = event.clientY + parseFloat(this.svg.attributes['viewBox'].split(" ")[1]);
    this.isPointerDown = true;
  }

  drag(event: any) {
    if (!this.isPointerDown) {
      return;
    }
    event.preventDefault();
    let newX = -(parseFloat(event.clientX) - this.offset['x']);
    let newY = -(parseFloat(event.clientY) - this.offset['y']);
    this.svg.attributes['viewBox'] = newX.toString() + " " + newY.toString() + " " +
     this.svg.attributes['viewBox'].split(" ")[2] + " " +  this.svg.attributes['viewBox'].split(" ")[3];  
  }

  endDrag(event: any) {
    this.isPointerDown = false;
  }

  mouseWheel(event: any) {
    event.preventDefault();
    if (Number(event.deltaY) > 0) {
      this.zoomOut();
    } else { 
      this.zoomIn();
    }
  }

  zoomOut() {
    let attributes = this.svg.children[0].attributes['transform'];
    let scaleFirst = attributes.split(" ")[0];
    let scaleSecond = attributes.split(" ")[1];
    let rotate = attributes.split(" ")[2];
    let translateFirst = attributes.split(" ")[3];
    let translateSecond = attributes.split(" ")[4];

    if ((parseFloat(scaleFirst.split(" ")[0].substring(6)) - 0.25) <= 0.0){
      return;
    }

    this.svg.children[0].attributes['transform'] = 
    "scale(" + (parseFloat(scaleFirst.split(" ")[0].substring(6)) - 0.25).toString() +
     " " + (parseFloat(scaleSecond.substring(0, scaleSecond.length - 1)) - 0.25).toString() +
      ") " + rotate + " " + translateFirst + " " + translateSecond; 

  }

  zoomIn() {
    let attributes = this.svg.children[0].attributes['transform'];
    let scaleFirst = attributes.split(" ")[0];
    let scaleSecond = attributes.split(" ")[1];
    let rotate = attributes.split(" ")[2];
    let translateFirst = attributes.split(" ")[3];
    let translateSecond = attributes.split(" ")[4];

    this.svg.children[0].attributes['transform'] =
    "scale(" + (parseFloat(scaleFirst.split(" ")[0].substring(6)) + 0.25).toString() +
     " " + (parseFloat(scaleSecond.substring(0, scaleSecond.length - 1)) + 0.25).toString() +
      ") " + rotate + " " + translateFirst + " " + translateSecond; 
  }

  resetGraph() {
    parse(this.copySVG).then((json) => this.setSvg(json));
  }

  resetZoom() {
    this.svg.children[0].attributes['transform'] = this.copyTransform;
    this.svg.attributes['viewBox'] = this.copyViewBox;
  }

  async loadPlayers() {    
    try {
      this.players = await firstValueFrom(this.restService.getPlayersIds());
    } catch (error) {
      console.log(error); 
    }
  }

  onGroupsChange(options: MatListOption[]) {
    this.resetAllHighlighting();
    this.selectedPlayersList = [];
    options.forEach(option => {
      this.selectedPlayersList.push(option.value);
    });
    this.higlightMorePlayersWrapper()
  }

  // Highligting for more players simutaneously
  higlightMorePlayersWrapper() {
    this.selectedPlayersList.forEach(player => {
      this.highlightSanboxRun(Number(player), 'red', 'red');
    });
  }

  resetAllHighlighting() {
    this.players.forEach(player => {
      this.highlightSanboxRun(player.valueOf(), "#09f5f5", "black");
    });
  }

  // Highlighting walkthrought part
  highlightWrapper(id: Number) {
    if (this.lastSelectedPlayer !== -1) {
      this.highlightSanboxRun(this.lastSelectedPlayer.valueOf(), "#09f5f5", "black");
    }
    this.lastSelectedPlayer = id;
    this.highlightSanboxRun(id.valueOf(), 'red', 'red');
  }

  highlightSanboxRun(id: number, colorNode: string, colorEdge: string) {
    for (let i = 0; i < this.svg.children[0].children.length; i++) {
      let element = this.svg.children[0].children[i];
      if (element.children[0].value === 'G') {
        continue;
      }
      if (element.attributes['class'] === 'edge') {
        if (this.isInJavaList(id, element.children[4].children[0]['value'])) {
          element.children[1].attributes['stroke'] = colorEdge;
        }
      } else {
        if (this.isInJavaList(id, element.children[3].children[0]['value'])) {
          element.children[1].attributes['fill'] = colorNode;
        }
      }
    }
  }

  isInJavaList(id: number, list: string) {
    let result = false;
    let individualIds = list.split(",");
    individualIds[0] = individualIds[0].substring(1);
    individualIds.forEach(currentId => {
      if (parseInt(currentId) === id) {
        result = true;
      }
    });
    return result;
  }

  resetWalkthroughSelection() {
    this.highlightSanboxRun(this.lastSelectedPlayer.valueOf(), "#09f5f5", "black");
    this.lastSelectedPlayer = -1;
  }

  delay(time: number) {
    return new Promise(resolve => setTimeout(resolve, time));
  }
}