package me.aarondmello.datatypes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Player {
    private String name;
    private String organization;
    private int score = 0;
    private final LinkedList<PlayerGameSummary> summaries = new LinkedList<>();
    private final HashMap<TiebreakType, Integer> tiebreaks = new HashMap<>();
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
    public LinkedList<PlayerGameSummary> getPlayerGameSummaries() {
        return summaries;
    }
    public Map<TiebreakType, Integer> getTiebreaks() {
        return tiebreaks;
    }
    public int getGamesAsBlack() {
        return summaries.stream().mapToInt(s -> ((s.getColour() == Colour.BLACK)?1:0)).sum();
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
        for (PlayerGameSummary p: playerGameSummaries) {
            summaries.add(p);
            score += p.getPointsEarned();
        }

    }

    
    public boolean hasSatOut(){
        return hasPlayedAgainst(NullPlayer.getInstance());
    }
    public boolean hasPlayedAgainst(Player opponent){
        return summaries.stream().anyMatch(m -> m.getOpponent().equals(opponent));
    }

    public int getScoreAgainst(Player opponent){
        for(PlayerGameSummary m : summaries){
            if(m.getOpponent().equals(opponent))
                return m.getPointsEarned();
        }
        return -1;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}