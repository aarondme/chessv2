package me.aarondmello.driver;
public class Match{
    Player white;
    Player black;

    Match(Player white, Player black){
        this.white = white;
        this.black = black;
    }

    public void setResult(int score) {
        sendWhiteResult(score);
        sendBlackResult(score);
    }
    private void sendWhiteResult(int result){
        if(white != null){
            MatchResult whiteResult = new MatchResult(Result.getPointsForPlayer(Colour.WHITE, result), 
                                                        black, Colour.WHITE);
            white.addMatchResults(whiteResult);
        }
    }
    private void sendBlackResult(int result){
        if(black != null){
            MatchResult blackResult = new MatchResult(Result.getPointsForPlayer(Colour.BLACK, result), 
                                                        white, Colour.BLACK);
            black.addMatchResults(blackResult);
        }
    }
    public Player getWhitePlayer() {
        return white;
    }
    public Player getBlackPlayer() {
        return black;
    }
}