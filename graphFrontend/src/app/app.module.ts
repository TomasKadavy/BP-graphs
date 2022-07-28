import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { SvgComponent } from './svg/svg.component';
import { GraphComponent } from './svg/graph/graph.component';
import { NodeComponent } from './svg/graph/node/node.component';
import { EdgeComponent } from './svg/graph/node/edge/edge.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatDialogModule } from "@angular/material/dialog";
import { MatButtonModule } from '@angular/material/button';

import { NavigationComponent } from './navigation/navigation.component';
import { ReinforcedSvgComponent } from './reinforced-svg/reinforced-svg.component';
import { PlayerSvgComponent } from './player-svg/player-svg.component';
import { LevelSvgComponent } from './level-svg/level-svg.component';
import { DialogGraphComponent } from './dialog-graph/dialog-graph.component';

const ROUTES: Routes = [
  {path: 'reinforcedGraph', component: ReinforcedSvgComponent},
  {path: 'playerGraph', component: PlayerSvgComponent},
  {path: 'levelGraph', component: LevelSvgComponent},
  {path: 'levelGraph/:id', component: LevelSvgComponent},
  {path: '**', component: ReinforcedSvgComponent}
]

@NgModule({
  declarations: [
    AppComponent,
    SvgComponent,
    GraphComponent,
    NodeComponent,
    EdgeComponent,
    NavigationComponent,
    ReinforcedSvgComponent,
    PlayerSvgComponent,
    LevelSvgComponent,
    DialogGraphComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatSidenavModule,
    MatToolbarModule,
    MatDialogModule,
    MatListModule,
    MatButtonModule,
    RouterModule.forRoot(ROUTES)
  ],
  exports: [RouterModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
