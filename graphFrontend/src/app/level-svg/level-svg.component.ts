import { Component } from '@angular/core';
import { AbstractGraph } from '../abstract-graph';
import { RestService } from '../rest.service';
import { firstValueFrom } from 'rxjs';
import { parse } from 'svgson';
import { ActivatedRoute } from '@angular/router';

/**
 * Component for showing individual SVG levels graphs
 */
@Component({
  selector: 'app-level-svg',
  templateUrl: './level-svg.component.html',
  styleUrls: ['./level-svg.component.css']
})
export class LevelSvgComponent extends AbstractGraph {
  levelId: Number = -1;

  constructor(restService: RestService, private route: ActivatedRoute) {
    super(restService, 'levelGraph');
    this.loadPlayers();

    this.route.params.subscribe((params: any) => {
      this.levelId = params['id'];

      this.levelGraph(this.levelId);
    });

  }

  async levelGraph(level: Number) {
    try {
      this.inputFile = await firstValueFrom(this.restService.getLevelGraph(level));
      if (this.inputFile.size === 0) {
        this.delay(1000).then(() => this.levelGraph(level));
      }
      let fileData = await this.readInputFile(this.inputFile) as string;
      parse(fileData).then((json) => this.setSvg(json));
      this.copySVG = fileData;
    } catch (error) {
      console.log(error);
    }
  }

}
