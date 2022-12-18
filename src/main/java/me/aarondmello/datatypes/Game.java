package me.aarondmello.datatypes;

public class Game{
    Player white;
    Player black;
    GameResult result;
    private int getPointsForPlayer(Colour playerColour, GameResult result){
        if(result == GameResult.DRAW) return 1;
        return (result == GameResult.WHITE_WIN ^ playerColour != Colour.WHITE)? 2:0;
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
        sendWhiteResult(result);
        sendBlackResult(result);
    }
    private void sendWhiteResult(GameResult result){
        PlayerGameSummary whiteResult = new PlayerGameSummary(getPointsForPlayer(Colour.WHITE, result),
                                                    black, Colour.WHITE);
        white.addPlayerGameSummary(whiteResult);
    }
    private void sendBlackResult(GameResult result){
        PlayerGameSummary blackResult = new PlayerGameSummary(getPointsForPlayer(Colour.BLACK, result),
                                                    white, Colour.BLACK);
        black.addPlayerGameSummary(blackResult);
    }
    public Player getWhitePlayer() {
        return white;
    }
    public Player getBlackPlayer() {
        return black;
    }

}