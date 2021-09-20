package me.aarondmello.driver;
import java.util.LinkedList;
class Tiebreaks{
    private int buchholzCutOne;
    private int buchholz;
    private int sonnebornBerger;
    private int progressiveScores;
    private int directEncounter; //needs to be implemented by tournament
    private int winCount;
    private int winCountAsBlack;
    Tiebreaks(){
        buchholzCutOne = 0;
        buchholz = 0;
        sonnebornBerger = 0;
        progressiveScores = 0;
        directEncounter = 0;
        winCount = 0;
        winCountAsBlack = 0;
    }
    public void updateTiebreaks(LinkedList<MatchResult> matchResults) {
        updateBuchholzCutOne(matchResults);
        updateBuchholz(matchResults);
        updateSonnebornBerger(matchResults);
        updateProgressiveScores(matchResults);
        updateWinCount(matchResults);
        updateWinCountAsBlack(matchResults);
    }
    private void updateBuchholzCutOne(LinkedList<MatchResult> matchResults){
        int minScore = Integer.MAX_VALUE;
        buchholzCutOne = 0;
        for(MatchResult m : matchResults){
            int opponentScore = m.getOpponent().getScore();
            if(opponentScore < minScore){
                if(minScore == Integer.MAX_VALUE){
                    minScore = opponentScore;
                }
                else{
                    buchholzCutOne += minScore;
                    minScore = opponentScore;
                }
            }
            else{
                buchholzCutOne += opponentScore;
            }
        }
    }
    private void updateBuchholz(LinkedList<MatchResult> matchResults){
        buchholz = 0;
        for(MatchResult m : matchResults){
            int opponentScore = m.getOpponent().getScore();
            buchholz += opponentScore;
        }
    }
    private void updateSonnebornBerger(LinkedList<MatchResult> matchResults){
        sonnebornBerger = 0;
        for(MatchResult m : matchResults){
            int opponentScore = m.getOpponent().getScore();
            sonnebornBerger += opponentScore * m.getScore();
        }
    }
    private void updateProgressiveScores(LinkedList<MatchResult> matchResults){
        progressiveScores = 0;
        for(MatchResult m : matchResults){
            int matchScore = m.getScore();
            progressiveScores += progressiveScores + matchScore;
        }
    }
    private void updateWinCount(LinkedList<MatchResult> matchResults){
        winCount = 0;
        for(MatchResult m : matchResults){
            if(m.getScore() == 2) winCount++;
        }
    }
    private void updateWinCountAsBlack(LinkedList<MatchResult> matchResults){
        winCountAsBlack = 0;
        for(MatchResult m : matchResults){
            if(m.getScore() == 2 && m.getColour() == Colour.BLACK) 
                winCountAsBlack++;
        }
    }
    
    public int getBuchholzCutOne() {
        return buchholzCutOne;
    }
    public int getBuchholz() {
        return buchholz;
    }
    public int getSonnebornBerger() {
        return sonnebornBerger;
    }
    public int getProgressiveScores() {
        return progressiveScores;
    }
    public int getDirectEncounter() {
        return directEncounter;
    }
    public int getWinCount() {
        return winCount;
    }
    public int getWinCountAsBlack() {
        return winCountAsBlack;
    }
    public void setDirectEncounter(int directEncounter) {
        this.directEncounter = directEncounter;
    }
}