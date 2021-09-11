public class Match{
    Player whitePlayer;
    Player blackPlayer;

    Match(Player whitePlayer, Player blackPlayer){
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    public void setResult(int result) {
        sendWhitePlayerResult(result);
        sendBlackPlayerResult(result);
    }
    private void sendWhitePlayerResult(int result){
        MatchResult whiteResult = new MatchResult(result, blackPlayer, Colour.WHITE);
        whitePlayer.addMatchResults(whiteResult);
    }
    private void sendBlackPlayerResult(int result){
        MatchResult blackResult = new MatchResult(2 - result, whitePlayer, Colour.BLACK);
        blackPlayer.addMatchResults(blackResult);
    }
}