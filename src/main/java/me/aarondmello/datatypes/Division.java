package me.aarondmello.datatypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.aarondmello.driver.PairingSystem;
import me.aarondmello.tiebreaks.TiebreakInitializer;

public class Division{
    private String name;
    private ArrayList<Player> players = new ArrayList<Player>();
    private int maxID = 0;
    private PairingSystem pairingSystem = new PairingSystem();
    private TiebreakInitializer tiebreakInitializer = new TiebreakInitializer();
    private Round currentRound;

    Division(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void sortPlayers(){
        Collections.sort(players, Collections.reverseOrder());
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
    public void initialize() {
        ArrayList<Integer> ids = new ArrayList<>();
        for(int i = 0; i < players.size(); i++)
            ids.add(i);
        Collections.shuffle(ids);
        for(int i = 0; i < players.size(); i++){
            Player player = players.get(i);
            tiebreakInitializer.initialize(player);
            player.setID(ids.get(i));
        }
        sortPlayers();
    }
    public void pairRound(int roundNumber) {
        currentRound = pairingSystem.pairRound(roundNumber, players);
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
    }
    public void confirmRoundResults() {
        for(Game game : currentRound.getGames())
            game.confirmResult();
        for(Player player : players)
            player.update();
        sortPlayers();
        //TODO update ranking-dependant tiebreaks
        //sortPlayers();
    }
    public boolean validateRoundResults() {
        for(Game game : currentRound.getGames()){
            if(!game.isResultValid())
                return false;
        }
        return true;
    }
    public void setGameResultByID(int id, int result) {
        currentRound.setResultByID(id, result);
    }
    public LinkedList<Game> getPairing() {
        return currentRound.getGames();
    }
}