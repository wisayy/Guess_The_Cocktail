package com.ridango.game.controller;


import com.ridango.game.model.Game;
import com.ridango.game.model.Player;
import com.ridango.game.service.GameService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/startGame")
    public Game startGame(@RequestParam String playerName) {
        return gameService.startGame(playerName);
    }

    @PostMapping("/guess")
    public Game guess(@RequestParam String playerName, @RequestParam String guess) {
        return gameService.processGuess(playerName, guess);
    }

    @GetMapping("/leaderboard")
    public List<Player> getLeaderboard() {
        return gameService.getLeaderboard();
    }
}