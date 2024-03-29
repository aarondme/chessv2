package me.aarondmello.datatypes;

/**
 * Handles sitting a player out. Any player sitting out
 * is paired with the NullPlayer.
 */
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
    public void addPlayerGameSummary(PlayerGameSummary ... PlayerGameSummary) {}

    @Override
    public int getID() {
        return -1;
    }

    @Override
    public boolean hasPlayedAgainst(Player opponent) {
        return false;
    }

    @Override
    public int getTiebreakScore(TiebreakType type){return 0;}

}
