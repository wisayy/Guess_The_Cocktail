import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { GameService } from '../game.service';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [FormsModule, HttpClientModule, CommonModule, RouterOutlet],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent implements OnInit {
  game: any = {
    playerName: '',
    cocktail: { strDrink: '', strDrinkThumb: '' },
    attemptsLeft: 0,
    score: 0,
    currentGuess: ''
  };
  guess: string = '';
  playerName: string = '';
  leaderboard: any[] = [];

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router, private gameService: GameService) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.playerName = params['playerName'];
      this.startGame();
      this.loadLeaderboard();
    });
  }

  loadLeaderboard(): void {
    this.gameService.getLeaderboard().subscribe((data: any[]) => {
      this.leaderboard = data;
    });
  }

  startGame() {
    this.gameService.startGame(this.playerName).subscribe(data => {
      this.game = data;
      this.loadLeaderboard();
    });
  }

  submitGuess() {
    this.gameService.submitGuess(this.playerName, this.guess).subscribe(data => {
      this.game = data;
      this.loadLeaderboard();
      if (this.game.attemptsLeft <= 0) {
        alert('Youre lost! The correct answer was: ' + this.game.cocktail.strDrink);
        this.startGame();
        this.loadLeaderboard();
      }
    });
  }


  returnToHome() {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem('isGameStarted');
    }
    this.router.navigate(['/']);
  }
}
