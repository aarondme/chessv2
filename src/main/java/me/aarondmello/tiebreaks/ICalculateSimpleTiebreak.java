package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.PlayerGameSummary;

import java.util.LinkedList;

interface ICalculateSimpleTiebreak {
    int calculateScore(LinkedList<PlayerGameSummary> games);

    TiebreakType type();
}
