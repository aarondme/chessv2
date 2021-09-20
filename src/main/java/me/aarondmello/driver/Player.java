package me.aarondmello.driver;
import java.util.LinkedList;

public class Player{
    private String name;
    private String organization;
    private int score;
    private int gamesAsWhite, gamesAsBlack;
    private LinkedList<GameResult> gameResults;
    private Tiebreaks tiebreaks;
    private boolean isPaired;

    Player(String name, String organization){
        this.name = name;
        this.organization = organization;
        this.score = 0;
        this.gameResults = new LinkedList<GameResult>();
        this.tiebreaks = new Tiebreaks();
        this.gamesAsBlack = 0;
        this.gamesAsWhite = 0;
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
    public LinkedList<GameResult> getGameResults() {
        return gameResults;
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
    public boolean isPaired(){
        return isPaired;
    } 
    public void setIsPaired(boolean isPaired){
        this.isPaired = isPaired;
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
    public void addGameResults(GameResult gameResult){
        gameResults.add(gameResult);
        score += gameResult.getPointsEarned();
    }
    
    public boolean hasSatOut(){
        for(GameResult m : gameResults){
            if(m.getOpponent() == null)
                return true;
        }
        return false;
    }
    public boolean hasPlayedAgainst(Player opponent){
        for(GameResult m : gameResults){
            if(m.getOpponent().equals(opponent))
                return true;
        }
        return false;
    }
}