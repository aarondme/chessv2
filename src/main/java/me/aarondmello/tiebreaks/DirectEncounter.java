package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.Player;

import java.util.Collection;
import java.util.Comparator;

public class DirectEncounter implements Tiebreak {
//TODO implement this class

    @Override
    public String name() {
        return TiebreakType.DirectEncounter.name();
    }

    @Override
    public void computeTiebreak(Collection<Player> players, Comparator<Player> playerComparator) {

    }
}
