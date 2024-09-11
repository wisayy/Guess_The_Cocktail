import {Component, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FormsModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'guess-the-cocktail';
  playerName: string = '';
  isGameStarted: boolean = false;

  constructor(private router: Router) {}

  ngOnInit() {
    if (typeof localStorage !== 'undefined') {
      const savedPlayerName = localStorage.getItem('playerName');
      const savedIsGameStarted = localStorage.getItem('isGameStarted');

      if (savedPlayerName) {
        this.playerName = savedPlayerName;
      }

      if (savedIsGameStarted === 'true') {
        this.isGameStarted = true;
        this.router.navigate(['/game'], { queryParams: { playerName: this.playerName } });
      }
    }
  }

  startGame() {
    if (this.playerName) {
      this.isGameStarted = true;
      if (typeof localStorage !== 'undefined') {
        localStorage.setItem('playerName', this.playerName);
        localStorage.setItem('isGameStarted', 'true');
      }
      this.router.navigate(['/game'], { queryParams: { playerName: this.playerName } });
    }
  }


}
