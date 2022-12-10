package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.PlayerGameSummary;

import java.util.LinkedList;

public class SonnebornBerger implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        for (PlayerGameSummary game : games) {
            int opponentScore = game.getOpponent().getScore();
            score += opponentScore * game.getPointsEarned();
        }
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.SonnebornBerger;
    }
}
