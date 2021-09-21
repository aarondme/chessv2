package me.aarondmello.constants;

public class GameResult {
    public final static int WHITE_WIN = 2;
    public final static int DRAW = 1;
    public final static int BLACK_WIN = 0;

    public final static int getPointsForPlayer(int playerColour, int result){
        return (playerColour == Colour.WHITE)? result:2-result;
    }
}
