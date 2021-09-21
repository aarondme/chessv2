package me.aarondmello.datatypes;

public class Tiebreaks{
    private int buchholzCutOne = 0;
    private int buchholz = 0;
    private int sonnebornBerger = 0;
    private int progressiveScores = 0;
    private int directEncounter = 0; //needs to be implemented by tournament
    private int winCount = 0;
    private int winCountAsBlack = 0;
    
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
    public void setBuchholzCutOne(int buchholzCutOne) {
        this.buchholzCutOne = buchholzCutOne;
    }
    public void setBuchholz(int buchholz) {
        this.buchholz = buchholz;
    }
    public void setSonnebornBerger(int sonnebornBerger) {
        this.sonnebornBerger = sonnebornBerger;
    }
    public void setProgressiveScores(int progressiveScores) {
        this.progressiveScores = progressiveScores;
    }
    public void setDirectEncounter(int directEncounter) {
        this.directEncounter = directEncounter;
    }
    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }
    public void setWinCountAsBlack(int winCountAsBlack) {
        this.winCountAsBlack = winCountAsBlack;
    }
}