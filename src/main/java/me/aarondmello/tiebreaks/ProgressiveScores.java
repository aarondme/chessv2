package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.PlayerGameSummary;

import java.util.LinkedList;

public class ProgressiveScores implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        int runningTotal = 0;
        for (PlayerGameSummary game : games) {
            int gameScore = game.getPointsEarned();
            runningTotal += gameScore;
            score += runningTotal;
        }
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.ProgressiveScores;
    }
}
