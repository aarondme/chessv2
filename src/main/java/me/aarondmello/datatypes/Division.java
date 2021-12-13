package me.aarondmello.datatypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.aarondmello.driver.PlayerComparator;
public class Division{
    private String name;
    private ArrayList<Player> players = new ArrayList<Player>();
    private int maxID = 0;
    
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
        p.setID(maxID++);
        players.add(p);
    }
    public void addPlayers(List<Player> players){
        players.addAll(players);
    }
    public List<Player> getPlayers(){
        return players;
    }
    public Player getPlayerById(int id){
        for(Player p : players){
            if(p.getID() == id)
                return p;
        }
        return null;
    }
    public void removePlayer(int playerID) {
        Iterator<Player> lIterator = players.iterator();
        while(lIterator.hasNext()){
            if(lIterator.next().getID() == playerID)
                lIterator.remove();
        }
    }
}