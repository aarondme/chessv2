package me.aarondmello.tiebreaks;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import me.aarondmello.constants.Colour;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.PlayerGameSummary;

public class WinCountAsBlack implements Tiebreak {


    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        for(PlayerGameSummary game : games){
            if(game.getPointsEarned() == 2 && game.getColour() == Colour.BLACK) 
                score++;
        }
        return score;
    }

    @Override
    public String name() {
        return TiebreakType.WinCountAsBlack.toString();
    }

    @Override
    public void computeTiebreak(Collection<Player> players, Comparator<Player> playerComparator) {
        for(Player p : players)
            p.setTiebreak(name(), calculateScore(p.getPlayerGameSummaries()));
    }
}
