package me.aarondmello.datatypes;

public record PlayerGameSummary(int pointsEarned, Player opponent,
                                Colour colour) {
    public int getPointsEarned() {
        return pointsEarned;
    }

    public Player getOpponent() {
        return opponent;
    }

    public Colour getColour() {
        return colour;
    }
}