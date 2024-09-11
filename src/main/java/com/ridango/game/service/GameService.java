package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import com.ridango.game.model.Game;
import com.ridango.game.model.Player;
import com.ridango.game.model.CocktailResponse;
import com.ridango.game.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GameService {

    private final PlayerRepository playerRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String COCKTAIL_API_URL = "https://www.thecocktaildb.com/api/json/v1/1/random.php";
    private Map<String, Game> ongoingGames = new HashMap<>();
    private Set<String> usedCocktails = new HashSet<>();

    public GameService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Game startGame(String playerName) {
        Player player = playerRepository.findByName(playerName)
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setName(playerName);
                    newPlayer.setScore(0);
                    return playerRepository.save(newPlayer);
                });

        Game game = new Game();
        game.setPlayerName(playerName);
        game.setAttemptsLeft(5);
        game.setScore(player.getScore());

        Cocktail cocktail = getRandomCocktail();
        while (usedCocktails.contains(cocktail.getStrDrink())) {
            cocktail = getRandomCocktail();
        }
        usedCocktails.add(cocktail.getStrDrink());

        game.setCocktail(cocktail);
        game.setCurrentGuess("_".repeat(cocktail.getStrDrink().length()));

        ongoingGames.put(playerName, game);

        return game;
    }

    public Game processGuess(String playerName, String guess) {
        Game game = ongoingGames.get(playerName);
        if (game == null) {
            throw new IllegalArgumentException("Game not found for player: " + playerName);
        }

        String correctName = game.getCocktail().getStrDrink();
        if (guess.equalsIgnoreCase(correctName)) {
            game.setScore(game.getScore() + 150); // Add 150 points for correct guess
            ongoingGames.remove(playerName);
            savePlayerScore(playerName, game.getScore());
            game = startGame(playerName);
        } else {
            game.setAttemptsLeft(game.getAttemptsLeft() - 1);
            game.setScore(Math.max(game.getScore() - 10, 0)); // Decrease score by 50 points but not below 0
            revealLetters(game);
            if (game.getAttemptsLeft() <= 0) {
                revealAllLetters(game);
                ongoingGames.remove(playerName);
                savePlayerScore(playerName, game.getScore());
            }
        }

        return game;
    }

    private void revealLetters(Game game) {
        String correctName = game.getCocktail().getStrDrink();
        String currentGuess = game.getCurrentGuess();

        StringBuilder revealedGuess = new StringBuilder(currentGuess);
        boolean letterRevealed = false;

        for (int i = 0; i < correctName.length(); i++) {
            if (currentGuess.charAt(i) == '_' && Math.random() > 0.7) {
                revealedGuess.setCharAt(i, correctName.charAt(i));
                letterRevealed = true;
            }
        }

        // Ensure at least one letter is revealed
        if (!letterRevealed) {
            for (int i = 0; i < correctName.length(); i++) {
                if (currentGuess.charAt(i) == '_') {
                    revealedGuess.setCharAt(i, correctName.charAt(i));
                    break;
                }
            }
        }

        game.setCurrentGuess(revealedGuess.toString());
    }

    private void revealAllLetters(Game game) {
        game.setCurrentGuess(game.getCocktail().getStrDrink());
    }

    private void savePlayerScore(String playerName, int score) {
        Player player = playerRepository.findByName(playerName)
                .orElse(new Player());

        player.setName(playerName);
        player.setScore(score);
        playerRepository.save(player);
    }

    public List<Player> getLeaderboard() {
        return playerRepository.findTop10ByOrderByScoreDesc();
    }

    private Cocktail getRandomCocktail() {
        return restTemplate.getForObject(COCKTAIL_API_URL, CocktailResponse.class).getDrinks().get(0);
    }


}