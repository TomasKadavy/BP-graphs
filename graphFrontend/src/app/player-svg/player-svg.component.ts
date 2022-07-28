import { Component, OnInit } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { RestService } from '../rest.service';
import { parse } from 'svgson';
import { AbstractGraph } from '../abstract-graph';

/**
 * Class for showing individual SVG graphs of players
 */
@Component({
  selector: 'app-player-svg',
  templateUrl: './player-svg.component.html',
  styleUrls: ['./player-svg.component.css']
})
export class PlayerSvgComponent extends AbstractGraph implements OnInit{
  constructor(restService: RestService) {
    super(restService, 'playerGraph')
    this.loadPlayers().then(() => this.individualGraph(this.players[0]));
  }

  ngOnInit(): void {
    this.loadPlayers().then(() => this.individualGraph(this.players[0]));
  }

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