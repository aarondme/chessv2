package me.aarondmello.tiebreaks;

import java.util.LinkedList;

import me.aarondmello.constants.Colour;
import me.aarondmello.datatypes.PlayerGameSummary;

public class WinCountAsBlack implements RankingIndependantTiebreak {
    private int score = 0;
    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void calculateScore(LinkedList<PlayerGameSummary> games) {
        score = 0;
        for(PlayerGameSummary game : games){
            if(game.getPointsEarned() == 2 && game.getColour() == Colour.BLACK) 
                score++;
        }
    }
}
