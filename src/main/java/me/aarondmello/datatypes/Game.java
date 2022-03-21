package me.aarondmello.datatypes;

import me.aarondmello.constants.Colour;
import me.aarondmello.constants.GameResult;

public class Game{
    Player white;
    Player black;
    int result = -1;
    public Game(Player white, Player black){
        this.white = white;
        this.black = black;
    }

    public void setResult(int result){
        if(0 <= result && result <= 2)
            this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void confirmResult() {
        sendWhiteResult(result);
        sendBlackResult(result);
    }
    private void sendWhiteResult(int result){
        PlayerGameSummary whiteResult = new PlayerGameSummary(GameResult.getPointsForPlayer(Colour.WHITE, result), 
                                                    black, Colour.WHITE);
        white.addPlayerGameSummary(whiteResult);
    }
    private void sendBlackResult(int result){
        PlayerGameSummary blackResult = new PlayerGameSummary(GameResult.getPointsForPlayer(Colour.BLACK, result), 
                                                    white, Colour.BLACK);
        black.addPlayerGameSummary(blackResult);
    }
    public Player getWhitePlayer() {
        return white;
    }
    public Player getBlackPlayer() {
        return black;
    }

    public boolean isResultValid() {
        return (0 <= result && result <= 2);
    }
}