import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class GameService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  startGame(playerName: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/startGame?playerName=${playerName}`);
  }

  submitGuess(playerName: string, guess: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/guess?playerName=${playerName}&guess=${encodeURIComponent(guess)}`, {});
  }

  getLeaderboard(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/leaderboard`);
  }
}
