package me.aarondmello.datatypes;

public class Game{
    Player white;
    Player black;
    GameResult result;
    private int getPointsForPlayer(Colour playerColour, GameResult result){
        if(result == GameResult.DRAW) return 1;
        return (result == GameResult.WHITE_WIN ^ playerColour == Colour.WHITE)? 0:2;
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

    public void confirmResult() {
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