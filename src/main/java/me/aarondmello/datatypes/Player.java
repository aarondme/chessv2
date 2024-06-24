package me.aarondmello.datatypes;

import java.util.LinkedList;
import java.util.Map;

public class Player {
    private String name;
    private String organization;
    private final PlayerResult results = new PlayerResult();
    private final LinkedList<PlayerGameSummary> summaries = new LinkedList<>();
    private int rank;
    private boolean isActive;
    /**
     * IDs for players within a division are expected to be unique integers 
     */
    private int id;

    public Player(String name, String organization){
        this.name = name;
        this.organization = organization;
        this.isActive = true;
    }
    public String getName() {
        return name;
    }
    public String getOrganization() {
        return organization;
    }
    public int getScore() {
        return results.getScore();
    }
    public PlayerResult getPlayerResult(){return results;}
    public LinkedList<PlayerGameSummary> getPlayerGameSummaries() {
        return summaries;
    }
    public Map<TiebreakType, Integer> getTiebreaks() {
        return results.getTiebreakScores();
    }
    public int getGamesAsBlack() {
        return (int) summaries.stream().filter(s -> (s.getColour() == Colour.BLACK)).count();
    }

    public int getID() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setOrganization(String organization) {
        this.organization = organization.trim();
    }
    public void setScore(int results) {
        this.results.setScore(results);
    }
    public void setID(int id){
        this.id = id;
    }
    public void addPlayerGameSummary(PlayerGameSummary ... playerGameSummaries){
        for (PlayerGameSummary p: playerGameSummaries) {
            summaries.add(p);
            this.results.setScore(this.results.getScore() + p.getPointsEarned());
        }

    }

    
    public boolean hasSatOut(){
        return hasPlayedAgainst(NullPlayer.getInstance());
    }
    public boolean hasPlayedAgainst(Player opponent){
        return summaries.stream().anyMatch(m -> m.getOpponent().equals(opponent));
    }

    public void clearTiebreaks() {
        results.getTiebreakScores().clear();
    }

    public void setTiebreak(TiebreakType tiebreakType, int value) {
        results.getTiebreakScores().put(tiebreakType, value);
    }

    public int getTiebreakScore(TiebreakType type) {
        Integer a = results.getTiebreakScores().get(type);
        if(a == null) return 0;
        else return a;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}