package me.aarondmello.datatypes;
import java.util.ArrayList;
import java.util.Collections;

import me.aarondmello.driver.PlayerComparator;
public class Division{
    private String name;
    private ArrayList<Player> players;
    private Round currentRound;
    Division(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void sortPlayers(PlayerComparator comparator){
        Collections.sort(players, comparator);
    }
    public void addPlayer(Player p){
        players.add(p);
    }
}