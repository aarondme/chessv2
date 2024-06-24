package me.aarondmello.datatypes;

import java.util.HashMap;

public class PlayerScore {
    private int score = 0;
    private final HashMap<TiebreakType, Integer> tiebreakScores = new HashMap<>();


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public HashMap<TiebreakType, Integer> getTiebreakScores() {
        return tiebreakScores;
    }

    public int getTiebreakScore(TiebreakType type) {
        return tiebreakScores.getOrDefault(type, 0);
    }
}
