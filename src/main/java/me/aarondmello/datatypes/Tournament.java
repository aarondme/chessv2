package me.aarondmello.datatypes;
import java.io.File;
import java.util.*;

import me.aarondmello.driver.PlayerComparator;
//TODO initialize by type.
public class Tournament{
    private String name;
    private LinkedList<Division> divisions;
    private PlayerComparator playerComparator;
    private int roundNumber, totalRounds;
    private File toSaveTo;

    public Tournament(){
        this.roundNumber = 1;
        this.totalRounds = -1;
        this.name = null;
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

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public void setType(String trim) {
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDataValid(){
        return name != null && totalRounds > 0;
    }
}