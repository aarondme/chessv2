package me.aarondmello.datatypes;

import java.util.LinkedList;
import java.util.Map;

public class Player {
    private String name;
    private String organization;
    private final PlayerScore score = new PlayerScore();
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
        return score.getScore();
    }
    public PlayerScore getPlayerScore(){return score;}
    public LinkedList<PlayerGameSummary> getPlayerGameSummaries() {
        return summaries;
    }
    public Map<TiebreakType, Integer> getTiebreaks() {
        return score.getTiebreakScores();
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
    public void setScore(int score) {
        this.score.setScore(score);
    }
    public void setID(int id){
        this.id = id;
    }
    public void addPlayerGameSummary(PlayerGameSummary ... playerGameSummaries){
        for (PlayerGameSummary p: playerGameSummaries) {
            summaries.add(p);
            this.score.setScore(this.score.getScore() + p.getPointsEarned());
        }

    }

    
    public boolean hasSatOut(){
        return hasPlayedAgainst(NullPlayer.getInstance());
    }
    public boolean hasPlayedAgainst(Player opponent){
        return summaries.stream().anyMatch(m -> m.getOpponent().equals(opponent));
    }

    public void clearTiebreaks() {
        score.getTiebreakScores().clear();
    }

    public void setTiebreak(TiebreakType tiebreakType, int value) {
        score.getTiebreakScores().put(tiebreakType, value);
    }

    public int getTiebreakScore(TiebreakType type) {
        Integer a = score.getTiebreakScores().get(type);
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