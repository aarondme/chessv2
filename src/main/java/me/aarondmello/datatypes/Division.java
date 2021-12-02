package me.aarondmello.datatypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.aarondmello.driver.PlayerComparator;
public class Division{
    private String name;
    private ArrayList<Player> players;
    
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
    public void addPlayers(List<Player> players){
        players.addAll(players);
    }
}