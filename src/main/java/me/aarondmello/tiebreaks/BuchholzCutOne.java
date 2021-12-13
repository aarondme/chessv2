package me.aarondmello.tiebreaks;

import java.util.LinkedList;

import me.aarondmello.datatypes.PlayerGameSummary;

public class BuchholzCutOne implements RankingIndependantTiebreak{
    private int score = 0;
    private String name = "BUCHHOLZ_CUT_ONE";
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
        int minOpponentScore = Integer.MAX_VALUE;
        score = 0;
        for(PlayerGameSummary game : games){
            int opponentScore = game.getOpponent().getScore();
            score += opponentScore;   
            if(opponentScore < minOpponentScore)
                minOpponentScore = opponentScore;
        }
        score -= minOpponentScore;
    }
    
}
