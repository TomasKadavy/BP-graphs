import { Component, OnInit } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { RestService } from '../rest.service';

/**
 * Component for navigation inside frontend
 */
@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {
  levels: Number[] = [];

  constructor(private restService: RestService) {
    this.loadLevels();
  }

  async loadLevels() {    
    try {
      this.levels = await firstValueFrom(this.restService.getLevels());
    } catch (error) {
      console.log(error); 
    }
  }

  ngOnInit(): void {
  }

}
