package me.aarondmello.datatypes;
import java.util.LinkedList;

public class Player{
    private String name;
    private String organization;
    private int score = 0;
    private int gamesAsWhite = 0;
    private int gamesAsBlack = 0;
    private LinkedList<PlayerGameSummary> PlayerGameSummarys = new LinkedList<PlayerGameSummary>();
    private Tiebreaks tiebreaks = new Tiebreaks();
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
    public LinkedList<PlayerGameSummary> getPlayerGameSummarys() {
        return PlayerGameSummarys;
    }
    public Tiebreaks getTiebreaks() {
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
    public void addPlayerGameSummarys(PlayerGameSummary PlayerGameSummary){
        PlayerGameSummarys.add(PlayerGameSummary);
        score += PlayerGameSummary.getPointsEarned();
    }
    
    public boolean hasSatOut(){
        for(PlayerGameSummary m : PlayerGameSummarys){
            if(m.getOpponent() == null)
                return true;
        }
        return false;
    }
    public boolean hasPlayedAgainst(Player opponent){
        for(PlayerGameSummary m : PlayerGameSummarys){
            if(m.getOpponent().equals(opponent))
                return true;
        }
        return false;
    }
}