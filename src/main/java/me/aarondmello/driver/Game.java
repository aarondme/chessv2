package me.aarondmello.driver;
public class Game{
    public final static int WHITE_WIN = 2;
    public final static int DRAW = 1;
    public final static int BLACK_WIN = 0;

    final static int getPointsForPlayer(int playerColour, int result){
        return (playerColour == Colour.WHITE)? result:2-result;
    }

    Player white;
    Player black;

    Game(Player white, Player black){
        this.white = white;
        this.black = black;
    }

    public void setResult(int score) {
        sendWhiteResult(score);
        sendBlackResult(score);
    }
    private void sendWhiteResult(int result){
        if(white != null){
            GameResult whiteResult = new GameResult(getPointsForPlayer(Colour.WHITE, result), 
                                                        black, Colour.WHITE);
            white.addGameResults(whiteResult);
        }
    }
    private void sendBlackResult(int result){
        if(black != null){
            GameResult blackResult = new GameResult(getPointsForPlayer(Colour.BLACK, result), 
                                                        white, Colour.BLACK);
            black.addGameResults(blackResult);
        }
    }
    public Player getWhitePlayer() {
        return white;
    }
    public Player getBlackPlayer() {
        return black;
    }
}