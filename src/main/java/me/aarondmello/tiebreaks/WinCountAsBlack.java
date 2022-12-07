package me.aarondmello.tiebreaks;

import me.aarondmello.constants.Colour;
import me.aarondmello.datatypes.PlayerGameSummary;

import java.util.LinkedList;

public class WinCountAsBlack implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        for (PlayerGameSummary game : games) {
            if (game.getPointsEarned() == 2 && game.getColour() == Colour.BLACK)
                score++;
        }
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.WinCountAsBlack;
    }
}
