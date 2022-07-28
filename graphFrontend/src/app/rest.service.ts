import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';

/**
 * Class for implementing rest services for the whole frontend
 */
@Injectable({
  providedIn: 'root'
})
export class RestService {
  // Where is the backend running and the port
  backend = 'http://'+location.hostname+':8080';
  /**
   * All types of possible API endpoints
   */
  events = '/graph/events';
  bash = '/graph/bash';
  msf = '/graph/msf';
  reinforcedGraph = '/graph/reinforcedGraph';
  playersIds = '/info/players';
  individualGraphs = '/graph/playerGraph';
  levelGraphs = '/graph/levelGraph';
  levels = '/info/levels';

  private httpClient: HttpClient;
  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  getGraph(): Observable<Blob> {
    return this.httpClient.get(this.backend + this.events, {responseType: 'blob'})
  }

  getMSFGraph(): Observable<Blob> {
    return this.httpClient.get(this.backend + this.msf, {responseType: 'blob'})
  }

  getBashGraph(): Observable<Blob> {
    return this.httpClient.get(this.backend + this.bash, {responseType: 'blob'})
  }

  getReinforcedGraph(): Observable<Blob> {
    return this.httpClient.get(this.backend + this.reinforcedGraph, {responseType: 'blob'})
  }

  getPlayersIds(): Observable<any> {
    return this.httpClient.get(this.backend + this.playersIds)
  }

  getLevels(): Observable<any> {
    return this.httpClient.get(this.backend + this.levels)
  }

  getIndividualPlayerGraph(id: Number) {
    return this.httpClient.get(this.backend + this.individualGraphs + "/" + id.valueOf().toString(), {responseType: 'blob'})
  }

  getLevelGraph(level: Number) {
    return this.httpClient.get(this.backend + this.levelGraphs + "/" + level.valueOf().toString(), {responseType: 'blob'} )
  }

}
