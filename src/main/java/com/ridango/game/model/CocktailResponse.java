package com.ridango.game.model;

import java.util.List;

public class CocktailResponse {
    private List<Cocktail> drinks;

    public List<Cocktail> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Cocktail> drinks) {
        this.drinks = drinks;
    }
}