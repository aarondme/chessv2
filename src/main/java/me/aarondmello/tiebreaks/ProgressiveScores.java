package me.aarondmello.tiebreaks;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.PlayerGameSummary;

public class ProgressiveScores implements Tiebreak {
    private int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        int runningTotal = 0;
        for(PlayerGameSummary game : games){
            int gameScore = game.getPointsEarned();
            runningTotal += gameScore;
            score += runningTotal;
        }
        return score;
    }

    @Override
    public String name() {
        return TiebreakType.ProgressiveScores.toString();
    }

    @Override
    public void computeTiebreak(Collection<Player> players, Comparator<Player> playerComparator) {
        for(Player p : players)
            p.setTiebreak(name(), calculateScore(p.getPlayerGameSummaries()));
    }
}
