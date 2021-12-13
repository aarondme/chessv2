package me.aarondmello.tiebreaks;

import java.util.LinkedList;

import me.aarondmello.datatypes.PlayerGameSummary;

public class WinCount implements RankingIndependantTiebreak {
    private int score = 0;
    private String name = "WIN_COUNT";

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void calculateScore(LinkedList<PlayerGameSummary> games) {
        score = 0;
        for(PlayerGameSummary game : games){
            if(game.getPointsEarned() == 2) 
                score++;
        }
        
    }
}
