package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.Player;

import java.util.Collection;
import java.util.Comparator;

public interface Tiebreak {
    String name();
    void computeTiebreak(Collection<Player> players, Comparator<Player> playerComparator);
}
