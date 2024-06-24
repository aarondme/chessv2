package me.aarondmello.datatypes;

import java.util.*;

public class Player {
    private String name;
    private String organization;
    private final HashMap<TiebreakType, Integer> tiebreakScores = new HashMap<>();
    private int score;
    private final List<PlayerGameSummary> summaries = new LinkedList<>();
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
        return score;
    }

    public List<PlayerGameSummary> getPlayerGameSummaries() {
        return summaries;
    }
    public Map<TiebreakType, Integer> getTiebreaks() {
        return tiebreakScores;
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
        this.score = score;
    }
    public void setID(int id){
        this.id = id;
    }
    public void addPlayerGameSummary(PlayerGameSummary ... playerGameSummaries){
        summaries.addAll(Arrays.asList(playerGameSummaries));
        score += Arrays.stream(playerGameSummaries).mapToInt(PlayerGameSummary::getPointsEarned).sum();
    }
    public void computeScore(){
        score = summaries.stream().mapToInt(PlayerGameSummary::getPointsEarned).sum();
    }

    
    public boolean hasSatOut(){
        return hasPlayedAgainst(NullPlayer.getInstance());
    }
    public boolean hasPlayedAgainst(Player opponent){
        return summaries.stream().anyMatch(m -> m.getOpponent().equals(opponent));
    }

    public void clearTiebreaks() {
        tiebreakScores.clear();
    }

    public void setTiebreak(TiebreakType tiebreakType, int value) {
        tiebreakScores.put(tiebreakType, value);
    }

    public int getTiebreakScore(TiebreakType type) {
        return tiebreakScores.getOrDefault(type, 0);
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