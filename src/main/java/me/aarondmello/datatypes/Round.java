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

    public void addGame(Game g){
        games.addLast(g);
    }

    public void removeGame(){
        games.pollLast();
    }
   
}