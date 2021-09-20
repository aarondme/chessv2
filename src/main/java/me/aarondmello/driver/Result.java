package me.aarondmello.driver;
final class Result{
    final static int WHITE_WIN = 2;
    final static int DRAW = 1;
    final static int BLACK_WIN = 0;

    final static int getPointsForPlayer(int playerColour, int result){
        return (playerColour == Colour.WHITE)? result:2-result;
    }
}