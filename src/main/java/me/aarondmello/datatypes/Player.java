package me.aarondmello.datatypes;
import java.util.ArrayList;
import java.util.LinkedList;

import me.aarondmello.tiebreaks.RankingIndependantTiebreak;
import me.aarondmello.tiebreaks.Tiebreak;

public class Player implements Comparable<Player>{
    private String name;
    private String organization;
    private int score = 0;
    private int gamesAsWhite = 0;
    private int gamesAsBlack = 0;
    private LinkedList<PlayerGameSummary> PlayerGameSummarys = new LinkedList<PlayerGameSummary>();
    private ArrayList<Tiebreak> tiebreaks = new ArrayList<Tiebreak>();
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
    public ArrayList<Tiebreak> getTiebreaks() {
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
    @Override
    public int compareTo(Player o) {
        int diff = score - o.getScore();
        if(diff != 0)
            return diff;

        ArrayList<Tiebreak> oTiebreaks = o.getTiebreaks();
        for(int i = 0; i < tiebreaks.size(); i++){
            diff = tiebreaks.get(i).getScore() - oTiebreaks.get(i).getScore();
            if(diff != 0)
                return diff;
        }

        return getID() - o.getID();
    }
    public void addTiebreak(Tiebreak t){
        tiebreaks.add(t);
    }
    public void update() {
        updateScore();
        updateTiebreaks();
    }
    private void updateTiebreaks() {
        for(Tiebreak t : tiebreaks){
            if(t instanceof RankingIndependantTiebreak){
                RankingIndependantTiebreak tiebreak = (RankingIndependantTiebreak) t;
                tiebreak.calculateScore(PlayerGameSummarys); 
            }   
        }
    }
    private void updateScore() {
        score = 0;
        for(PlayerGameSummary m : PlayerGameSummarys)
            score += m.getPointsEarned();
    }
}