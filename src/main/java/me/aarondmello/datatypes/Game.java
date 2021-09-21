package me.aarondmello.datatypes;

import me.aarondmello.constants.Colour;
import me.aarondmello.constants.GameResult;

public class Game{
    Player white;
    Player black;

    public Game(Player white, Player black){
        this.white = white;
        this.black = black;
    }

    public void setResult(int score) {
        sendWhiteResult(score);
        sendBlackResult(score);
    }
    private void sendWhiteResult(int result){
        if(white != null){
            PlayerGameSummary whiteResult = new PlayerGameSummary(GameResult.getPointsForPlayer(Colour.WHITE, result), 
                                                        black, Colour.WHITE);
            white.addPlayerGameSummarys(whiteResult);
        }
    }
    private void sendBlackResult(int result){
        if(black != null){
            PlayerGameSummary blackResult = new PlayerGameSummary(GameResult.getPointsForPlayer(Colour.BLACK, result), 
                                                        white, Colour.BLACK);
            black.addPlayerGameSummarys(blackResult);
        }
    }
    public Player getWhitePlayer() {
        return white;
    }
    public Player getBlackPlayer() {
        return black;
    }
}