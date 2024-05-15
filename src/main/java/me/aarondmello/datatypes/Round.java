package me.aarondmello.datatypes;
import java.util.*;
public class Round{
    private final LinkedList<Game> games;
    public Round(){
        games = new LinkedList<>();
    }
    
    public LinkedList<Game> getGames() {
        return games;
    }

    public void addGame(Game g){
        games.addLast(g);
    }

    public void setResultByID(int id, GameResult result) {
        if(0 <= id && id < games.size())
            games.get(id).setResult(result);
        else
            System.err.println("Invalid ID provided, no game was changed");
    }
}