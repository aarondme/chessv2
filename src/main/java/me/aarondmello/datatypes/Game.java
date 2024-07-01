package me.aarondmello.datatypes;

public class Game{
    Player white;
    Player black;
    GameResult result;
    public static final int DRAW_POINTS = 1;
    public static final int WIN_POINTS = 2;
    public static final int LOSS_POINTS = 0;
    private int getPointsForPlayer(Colour playerColour, GameResult result){
        if(result == GameResult.DRAW) return DRAW_POINTS;
        return (result == GameResult.WHITE_WIN ^ playerColour == Colour.WHITE)? LOSS_POINTS:WIN_POINTS;
    }
    public Game(Player white, Player black){
        this.white = white;
        this.black = black;
    }

    public void setResult(GameResult result){
        this.result = result;
    }

    public GameResult getResult() {
        return result;
    }

    void confirmResult() {
        white.addPlayerGameSummary(
                new PlayerGameSummary(getPointsForPlayer(Colour.WHITE, result), black, Colour.WHITE)
        );
        black.addPlayerGameSummary(
                new PlayerGameSummary(getPointsForPlayer(Colour.BLACK, result), white, Colour.BLACK)
        );
    }

    public Player getWhitePlayer() {
        return white;
    }
    public Player getBlackPlayer() {
        return black;
    }

}