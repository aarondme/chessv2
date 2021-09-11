enum Colour{
    WHITE, BLACK
}
public class MatchResult {
    private int score;
    private Player opponent;
    private Colour colour;
    MatchResult(int score, Player opponent, Colour colour){
        this.score = score;
        this.opponent = opponent;
        this.colour = colour;
    }
    public int getScore() {
        return score;
    }
    public Player getOpponent() {
        return opponent;
    }
    public Colour getColour() {
        return colour;
    }
}