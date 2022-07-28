import { Component, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Inject } from '@angular/core';
import { AbstractGraph } from '../abstract-graph';
import { RestService } from '../rest.service';
import { firstValueFrom } from 'rxjs';
import { parse } from 'svgson';

/**
 * Component for showing player graphs inside dialogs for reinforced graph page
 */
@Component({
  selector: 'app-dialog-graph',
  templateUrl: './dialog-graph.component.html',
  styleUrls: ['./dialog-graph.component.css']
})
export class DialogGraphComponent extends AbstractGraph implements OnInit {
  constructor(@Inject(MAT_DIALOG_DATA) public playerId: any, restService: RestService) {
    super(restService, 'playerGraph');
    this.individualGraph(playerId['id']);
  }
 
  ngOnInit() {}

  async individualGraph(id: Number) {
    try {
      this.inputFile = await firstValueFrom(this.restService.getIndividualPlayerGraph(id));
      if (this.inputFile.size === 0) {
        this.delay(1000).then(() => this.individualGraph(id));
      }
      let fileData = await this.readInputFile(this.inputFile) as string;
      parse(fileData).then((json) => this.setSvg(json));
      this.copySVG = fileData;
      this.lastSelectedPlayer = id;
    } catch (error) {
      console.log(error);
    }
  }
}
