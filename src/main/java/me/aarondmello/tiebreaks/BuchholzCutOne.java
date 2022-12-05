package me.aarondmello.tiebreaks;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.PlayerGameSummary;

public class BuchholzCutOne implements Tiebreak{
    private int calculateScore(LinkedList<PlayerGameSummary> games) {
        int minOpponentScore = Integer.MAX_VALUE;
        int score = 0;
        for(PlayerGameSummary game : games){
            int opponentScore = game.getOpponent().getScore();
            score += opponentScore;   
            if(opponentScore < minOpponentScore)
                minOpponentScore = opponentScore;
        }
        score -= minOpponentScore;
        return score;
    }

    @Override
    public String name() {
        return TiebreakType.BuchholzCutOne.toString();
    }

    @Override
    public void computeTiebreak(Collection<Player> players, Comparator<Player> playerComparator) {
        for(Player p: players)
            p.setTiebreak(name(), calculateScore(p.getPlayerGameSummaries()));
    }
}
