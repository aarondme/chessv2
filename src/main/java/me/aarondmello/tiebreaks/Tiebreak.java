package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.Player;

import java.util.List;
import java.util.Comparator;

public interface Tiebreak {
    String name();
    void computeTiebreak(List<Player> players, Comparator<Player> playerComparator);

    TiebreakType type();
}

