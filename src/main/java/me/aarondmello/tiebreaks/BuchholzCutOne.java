package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.PlayerGameSummary;

import java.util.LinkedList;

public class BuchholzCutOne implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int minOpponentScore = 0;
        int score = 0;
        for (PlayerGameSummary game : games) {
            int opponentScore = game.getOpponent().getScore();
            score += opponentScore;
            if (opponentScore < minOpponentScore)
                minOpponentScore = opponentScore;
        }
        score -= minOpponentScore;
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.BuchholzCutOne;
    }
}
