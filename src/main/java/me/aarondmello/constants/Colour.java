package me.aarondmello.constants;
public final class Colour{
    public final static int WHITE = 1;
    public final static int BLACK = 0;

    final static boolean isColourWhite(int colour){
        return colour == WHITE;
    }

    final static boolean isColourBlack(int colour){
        return colour == BLACK;
    }
}