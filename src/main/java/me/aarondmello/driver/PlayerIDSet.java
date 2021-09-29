package me.aarondmello.driver;

import me.aarondmello.datatypes.Player;

public class PlayerIDSet {
    boolean[] playerIDPresent;

    public PlayerIDSet(int numberOfPlayers){
        playerIDPresent = new boolean[numberOfPlayers];
    }

    public void add(Player p) {
        playerIDPresent[p.getID()] = true;
    }

    public void remove(Player p) {
        playerIDPresent[p.getID()] = false;
    }

    public boolean contains(Player p) {
        return playerIDPresent[p.getID()];
    }

}
