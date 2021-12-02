package me.aarondmello.datatypes;
import java.util.*;

//TODO initialize by type.
public class Tournament{
    private String name;
    private LinkedList<Division> divisions;
    private int roundNumber, totalRounds;

    public Tournament(){
        this.roundNumber = 1;
        this.totalRounds = -1;
        this.name = null;
        this.divisions = new LinkedList<Division>();
    }

    public String getName() {
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

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDataValid(){
        return name != null && totalRounds > 0 && divisions.size() > 0;
    }

    public void addPlayersToTournament(Map<String, List<Player>> divisionNameToPlayerListMap){
        for (String divisionName : divisionNameToPlayerListMap.keySet()) {
            Division division = getDivisionWithName(divisionName);
            division.addPlayers(divisionNameToPlayerListMap.get(divisionName));
        }
    }

    private Division getDivisionWithName(String name){
        for (Division division : divisions) {
            if(division.getName().equals(name))
                return division;
        }
        Division division = new Division(name);
        divisions.add(division);
        return division;
    }
}