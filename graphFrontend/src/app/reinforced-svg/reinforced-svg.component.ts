import { Component, OnInit } from '@angular/core';
import { RestService } from '../rest.service';
import { firstValueFrom } from 'rxjs';
import { parse } from 'svgson';
import { AbstractGraph } from '../abstract-graph';
import { MatListOption } from '@angular/material/list';
import { MatDialog } from "@angular/material/dialog";
import { DialogGraphComponent } from '../dialog-graph/dialog-graph.component';

/**
 * Component for showing the reinforced SVG graph
 */
@Component({
  selector: 'app-reinforced-svg',
  templateUrl: './reinforced-svg.component.html',
  styleUrls: ['./reinforced-svg.component.css']
})
export class ReinforcedSvgComponent extends AbstractGraph{

  constructor(restService: RestService, private dialog: MatDialog) {
    super(restService, 'normal');
    this.loadPlayers();
  }

  openDialog(playerId: Number) {
    const dialogRef = this.dialog.open(DialogGraphComponent, {
      height: '90%',
      width: '90%',
      data: {
        id: playerId
      }
    });
  }

  async landingGraph(msf: boolean, bash: boolean) {
    try {
      // Only events
      if (!msf && !bash) {
        this.inputFile = await firstValueFrom(this.restService.getGraph());
        if (this.inputFile.size === 0) {
          this.delay(1000).then(() => this.landingGraph(msf, bash));
        }
      // Events and msf commands
      } else if (msf && !bash) {
        this.inputFile = await firstValueFrom(this.restService.getMSFGraph());
        if (this.inputFile.size === 0) {
          this.delay(1000).then(() => this.landingGraph(msf, bash));
        }
      // Events and bash commands  
      } else if (!msf && bash) {
        this.inputFile = await firstValueFrom(this.restService.getBashGraph());
        if (this.inputFile.size === 0) {
          this.delay(1000).then(() => this.landingGraph(msf, bash));
        }
      // Events, bash and msf commands    
      } else {
        this.inputFile = await firstValueFrom(this.restService.getReinforcedGraph());
        if (this.inputFile.size === 0) {
          this.delay(1000).then(() => this.landingGraph(msf, bash));
        }
      }

      let fileData = await this.readInputFile(this.inputFile) as string;
      parse(fileData).then((json) => this.setSvg(json));
      this.copySVG = fileData;
    } catch (error) {
      console.log(error);
    }
  }

  typesChanged(options: MatListOption[]) {
    if (options.length === 0) {
      this.landingGraph(false, false);
      return;
    }
    if (options.length === 2) {
      this.landingGraph(true, true);
      return;
    }
    if (options.length === 1) {
      if (options[0].value === "bash") {
        this.landingGraph(false, true);
        return;
      }
      this.landingGraph(true, false);
    }
  }
}