package me.aarondmello.driver;
import java.util.ArrayList;
import java.util.Collections;
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
    public void createRound(int roundNumber, int totalRounds){
        currentRound = new Round();
        if(roundNumber == 1)
            currentRound.pairFirstRound(players);
        else
            currentRound.pairSubsequentRounds(players);
    }
    public void sortPlayers(PlayerComparator comparator){
        Collections.sort(players, comparator);
    }
    public void addPlayer(Player p){
        players.add(p);
    }
}