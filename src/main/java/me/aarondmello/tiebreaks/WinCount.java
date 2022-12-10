package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.PlayerGameSummary;

import java.util.LinkedList;

public class WinCount implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        for (PlayerGameSummary game : games) {
            if (game.getPointsEarned() == 2)
                score++;
        }
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.WinCount;
    }
}
