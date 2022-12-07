package me.aarondmello.datatypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import me.aarondmello.tiebreaks.Tiebreak;
import me.aarondmello.tiebreaks.TiebreakType;

public class Player {
    private String name;
    private String organization;
    private int score = 0;
    private int gamesAsWhite = 0;
    private int gamesAsBlack = 0;
    private LinkedList<PlayerGameSummary> summaries = new LinkedList<>();
    private HashMap<TiebreakType, Integer> tiebreaks = new HashMap<>();
    /**
     * IDs for players within a division are expected to be unique integers 
     */
    private int id;

    public Player(String name, String organization){
        this.name = name;
        this.organization = organization;
    }
    public String getName() {
        return name;
    }
    public String getOrganization() {
        return organization;
    }
    public int getScore() {
        return score;
    }
    public LinkedList<PlayerGameSummary> getPlayerGameSummaries() {
        return summaries;
    }
    public Map<TiebreakType, Integer> getTiebreaks() {
        return tiebreaks;
    }
    public int getGamesAsBlack() {
        return gamesAsBlack;
    }
    public int getGamesAsWhite() {
        return gamesAsWhite;
    }
    public String getDisplayName() {
        if (organization.trim().length() == 0)
          return name;
        return (name + " (" + organization + ")");
    }
    public int getID() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void setID(int id){
        this.id = id;
    }
    public void addPlayerGameSummary(PlayerGameSummary playerGameSummary){
        summaries.add(playerGameSummary);
        score += playerGameSummary.getPointsEarned();
    }
    
    public boolean hasSatOut(){
        return hasPlayedAgainst(NullPlayer.getInstance());
    }
    public boolean hasPlayedAgainst(Player opponent){
        for(PlayerGameSummary m : summaries){
            if(m.getOpponent().equals(opponent))
                return true;
        }
        return false;
    }

    public void clearTiebreaks() {
        tiebreaks.clear();
    }

    public void setTiebreak(TiebreakType tiebreakType, int value) {
        tiebreaks.put(tiebreakType, value);
    }

    public int getTiebreakScore(TiebreakType type) {
        Integer a = tiebreaks.get(type);
        if(a == null) return 0;
        else return a;
    }
}