package me.aarondmello.datatypes;

public class NullPlayer extends Player{

    static NullPlayer instanceNullPlayer = new NullPlayer();

    NullPlayer() {
        super("BYE", "BYE");
    }

    public static NullPlayer getInstance(){
        return instanceNullPlayer;
    }
    
    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public void addPlayerGameSummarys(PlayerGameSummary PlayerGameSummary) {}

    @Override
    public void update() {}

    @Override
    public int getID() {
        return -1;
    }

    @Override
    public boolean hasPlayedAgainst(Player opponent) {
        return false;
    }

}
