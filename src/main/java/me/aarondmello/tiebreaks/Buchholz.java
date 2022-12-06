package me.aarondmello.tiebreaks;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.PlayerGameSummary;

public class Buchholz implements Tiebreak {
    private int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        for(PlayerGameSummary game : games){
            int opponentScore = game.getOpponent().getScore();
            score += opponentScore;
        }
        return score;
    }

    @Override
    public String name() {
        return TiebreakType.Buchholz.toString();
    }

    @Override
    public void computeTiebreak(Collection<Player> players, Comparator<Player> playerComparator) {
        for(Player p : players)
            p.setTiebreak(name(), calculateScore(p.getPlayerGameSummaries()));
    }
}
