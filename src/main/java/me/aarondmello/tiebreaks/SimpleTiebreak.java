package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.Player;

import java.util.Comparator;
import java.util.List;

public class SimpleTiebreak implements Tiebreak{
    ICalculateSimpleTiebreak calculator;
    public SimpleTiebreak(ICalculateSimpleTiebreak iCalculateSimpleTiebreak){
         calculator = iCalculateSimpleTiebreak;
    }

    @Override
    public String name() {
        return calculator.type().name();
    }

    @Override
    public TiebreakType type(){return calculator.type();}

    @Override
    public void computeTiebreak(List<Player> players, Comparator<Player> playerComparator) {
        for(Player p : players)
            p.setTiebreak(calculator.type(), calculator.calculateScore(p.getPlayerGameSummaries()));
    }
}

