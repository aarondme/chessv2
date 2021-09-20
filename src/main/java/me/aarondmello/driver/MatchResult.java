package me.aarondmello.driver;
public class MatchResult {
    private int score;
    private Player opponent;
    private int colour;
    MatchResult(int score, Player opponent, int colour){
        this.score = score;
        this.opponent = opponent;
        this.colour = colour;
    }
    public int getScore() {
        return score;
    }
    public Player getOpponent() {
        return opponent;
    }
    public int getColour() {
        return colour;
    }
}