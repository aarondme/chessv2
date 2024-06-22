package me.aarondmello.datatypes;
import java.util.*;

public class Tournament{
    private String name;
    private final LinkedList<Division> divisions = new LinkedList<>();
    private int roundNumber = 1;
    private int totalRounds;
    private boolean isRegionalTournament;

    
    public Tournament(String name, int totalRounds, boolean isRegionalTournament){
        this.name = name;
        this.totalRounds = totalRounds;
        this.isRegionalTournament = isRegionalTournament;
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

    public Division getDivisionWithName(String name, boolean createIfNotFound){
        for (Division division : divisions) {
            if(division.getName().equals(name))
                return division;
        }
        if(createIfNotFound){
            Division division = new Division(name);
            division.setTotalRounds(totalRounds);
            divisions.add(division);
            return division;
        }
        return null;
    }

    public Division getDivisionWithName(String name){
        return getDivisionWithName(name, false);
    }

    public Player getPlayer(String divisionName, int playerID){
        Division division = getDivisionWithName(divisionName);
        if(division == null)
            return null;
        return division.getPlayerById(playerID);
    }

    public void removePlayer(String divisionName, int playerID){
        Division division = getDivisionWithName(divisionName);
        if(division != null)
            division.removePlayer(playerID);
    }

    public void initialize(boolean shouldRandomize) {
        for(Division division : divisions){
            if (shouldRandomize) division.randomizeIds();
            division.initialize();
            division.setTotalRounds(totalRounds);
            division.setRegional(isRegionalTournament);
        }
    }

    public void createRound() {
        divisions.forEach(d -> d.pairRound(roundNumber));
    }

    public boolean confirmRoundResults() {
        if(divisions.stream().anyMatch(d -> !d.validateRoundResults()))
            return false;
        divisions.forEach(Division::confirmRoundResults);

        roundNumber++;
        return true;
    }

    public void setResultByDivisionAndGameID(String divisionName, int id, GameResult result) {
        Division division = getDivisionWithName(divisionName);
        if(division != null)
            division.setGameResultByID(id, result);
    }

    public void toggleType() {
        isRegionalTournament = !isRegionalTournament;
    }

    public boolean isRegionalTournament() {
        return isRegionalTournament;
    }

    public void setRegionalTournament(boolean regionalTournament) {
        isRegionalTournament = regionalTournament;
    }
}