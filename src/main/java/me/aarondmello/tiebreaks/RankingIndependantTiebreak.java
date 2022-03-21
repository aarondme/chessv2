package me.aarondmello.tiebreaks;

import java.util.LinkedList;

import me.aarondmello.datatypes.PlayerGameSummary;

public interface RankingIndependantTiebreak extends Tiebreak {
    void calculateScore(LinkedList<PlayerGameSummary> games);
}
