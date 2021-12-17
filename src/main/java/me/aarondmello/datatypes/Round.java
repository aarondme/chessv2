package me.aarondmello.datatypes;
import java.util.*;
public class Round{
    private LinkedList<Game> games;
    public Round(){
        games = new LinkedList<>();
    }
    
    public LinkedList<Game> getGames() {
        return games;
    }

    public int getNumberOfGames(){return games.size();}

    public void addAllGames(Collection<Game> toAdd){games.addAll(toAdd);}

    public void addGame(Game g){
        games.addLast(g);
    }

    public Game removeGame(){
        return games.pollLast();
    }

    public void setResultByID(int id, int result) {
        try {
            games.get(id).setResult(result);
        } catch (Exception e) {
        }
    }
}