package me.aarondmello.datatypes;
import java.util.*;

//TODO initialize by type.
public class Tournament{
    private String name = null;
    private LinkedList<Division> divisions = new LinkedList<>();
    private int roundNumber = 1;
    private int totalRounds = -1;

    public Tournament(){
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
        return (roundNumber <= totalRounds);
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public boolean isDataValid(){
        return name != null && totalRounds > 0 && divisions.size() > 0;
    }

    public void addPlayers(HashMap<String, ArrayList<Player>> divisionNameToPlayerListMap){
        for (String divisionName : divisionNameToPlayerListMap.keySet()) {
            Division division = getDivisionWithName(divisionName, true);
            division.addPlayers(divisionNameToPlayerListMap.get(divisionName));
        }
    }

    public void addPlayer(String divisionName, Player player){
        Division division = getDivisionWithName(divisionName, true);
        division.addPlayer(player);
    }

    public Division getDivisionWithName(String name, boolean createIfDoesntExist){
        for (Division division : divisions) {
            if(division.getName().equals(name))
                return division;
        }
        if(createIfDoesntExist){
            Division division = new Division(name);
            divisions.add(division);
            return division;
        }
        return null;
    }

    public Player getPlayer(String divisionName, int playerID){
        Division division = getDivisionWithName(divisionName, false);
        if(division == null)
            return null;
        return division.getPlayerById(playerID);
    }

    public void removePlayer(String divisionName, int playerID){
        Division division = getDivisionWithName(divisionName, false);
        if(division != null)
            division.removePlayer(playerID);
    }

    public void initialize(boolean shouldRandomize) {
        for(Division division : divisions){
            if (shouldRandomize) division.randomizeIds();
            division.initialize();
            division.setTotalRounds(totalRounds);
        }
    }

    public void createRound() {
        for(Division division : divisions)
            division.pairRound(roundNumber);
    }

    public boolean confirmRoundResults() {
        for(Division division : divisions){
            if(!division.validateRoundResults())
                return false;
        }
        for(Division division : divisions){
            division.confirmRoundResults();
        }    
        roundNumber++;
        return true;
    }

    public void setResultByDivisionAndGameID(String divisionName, int id, int result) {
        Division division = getDivisionWithName(divisionName, false);
        if(division != null)
            division.setGameResultByID(id, result);
    }
}