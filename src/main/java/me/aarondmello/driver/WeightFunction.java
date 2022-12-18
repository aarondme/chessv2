package me.aarondmello.driver;

import me.aarondmello.datatypes.Player;

import java.util.List;

public interface WeightFunction {
    Constraint getWeightConstraint(int bestWeight);
    int calculateWeight(int opponentIndex, Player p, int roundIndex, List<Player> players);
}
