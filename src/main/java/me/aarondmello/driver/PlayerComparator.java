package me.aarondmello.driver;
import java.util.*;
import java.util.function.BiFunction;

import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Tiebreaks;
public class PlayerComparator implements Comparator<Player>{
    Comparator<Tiebreaks> tiebreakConfig;
    PlayerComparator(Comparator<Tiebreaks> tiebreakConfig){
        this.tiebreakConfig = tiebreakConfig;
    }
    @Override
    public int compare(Player o1, Player o2) {
        List<BiFunction<Player, Player, Integer>> methodlist = 
            Arrays.asList(((Player a, Player b) -> {return b.getScore() - a.getScore();}),
                          ((Player a, Player b) -> {return tiebreakConfig.compare(a.getTiebreaks(),b.getTiebreaks());}),
                          ((Player a, Player b) -> {return a.getID() - b.getID();}));
        for(BiFunction<Player, Player, Integer> m : methodlist){
            int x = m.apply(o1, o2);
            if(x != 0) return x;
        }
        return 0;
    }
}