package me.aarondmello.driver;

import me.aarondmello.datatypes.Player;

import java.util.List;

public interface WeightFunction {
    int getBestWeightPossible(PairingSystem.State s, int[] coordinate, int opponentIndex, int weight, List<Player> players);
    int calculateWeight(int opponentIndex, Player p, int roundIndex, List<Player> players);
}
