package me.aarondmello.driver;
public class GameResult {
    private int pointsEarned;
    private Player opponent;
    private int colour;
    public GameResult(int pointsEarned, Player opponent, int colour){
        this.pointsEarned = pointsEarned;
        this.opponent = opponent;
        this.colour = colour;
    }
    public int getPointsEarned() {
        return pointsEarned;
    }
    public Player getOpponent() {
        return opponent;
    }
    public int getColour() {
        return colour;
    }
}