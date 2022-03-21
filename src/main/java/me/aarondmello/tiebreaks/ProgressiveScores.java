package me.aarondmello.tiebreaks;

import java.util.LinkedList;

import me.aarondmello.datatypes.PlayerGameSummary;

public class ProgressiveScores implements RankingIndependantTiebreak {

    private int score = 0;
    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void calculateScore(LinkedList<PlayerGameSummary> games) {
        score = 0;
        int runningTotal = 0;
        for(PlayerGameSummary game : games){
            int gameScore = game.getPointsEarned();
            runningTotal += gameScore;
            score += runningTotal;
        }
    }
    
}
