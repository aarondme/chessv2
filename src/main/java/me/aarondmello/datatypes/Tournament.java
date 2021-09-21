package me.aarondmello.datatypes;
import java.io.File;
import java.util.*;

import me.aarondmello.driver.PlayerComparator;

public class Tournament{
    private String name;
    private LinkedList<Division> divisions;
    private PlayerComparator playerComparator;
    private int roundNumber, totalRounds;
    private File toSaveTo;

    Tournament(String name, int totalRounds, File toSaveTo){
        this.name = name;
        this.totalRounds = totalRounds;
        this.toSaveTo = toSaveTo;
        this.roundNumber = 1;
    }

    public String getTournamentName() {
        return name;
    }
    public LinkedList<Division> getDivisions() {
        return divisions;
    }
    public int getRoundNumber() {
        return roundNumber;
    }
    public int getTotalRounds() {
        return totalRounds;
    }
    public boolean hasRoundsRemaining(){
        return (roundNumber < totalRounds);
    }
}