package me.aarondmello.driver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import me.aarondmello.datatypes.Game;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Round;

public class PairingSystem {
    Round round;
    PlayerIDSet playersPaired;
    ArrayList<Player> players;

    public PairingSystem(Round round, ArrayList<Player> players){
        this.round = round;
        this.players = players;
        playersPaired = new PlayerIDSet(players.size());
    }

    public PairingSystem(Round round){
        this.round = round;
    }

    public void setPlayers(ArrayList<Player> players){
        this.players = players;
        playersPaired = new PlayerIDSet(players.size());
    }

    /**
     * Populates the round with the proper games. 
     * @param roundNumber the round number
     */
    public void pairRound(int roundNumber){
        if(roundNumber == 1)
            pairFirstRound();
        else
            pairBruteForce(players.size());
    }

    private void pairFirstRound(){
        int frontPointer = 0;
        int backPointer = players.size() - 1;
        while(frontPointer < backPointer){
            Player white = players.get(frontPointer);
            Player black = players.get(backPointer);
            round.addGame(new Game(white, black));
            frontPointer++;
            backPointer--;
        }
        if(frontPointer == backPointer)
            round.addGame(new Game(players.get(frontPointer), null));
    }

    private boolean pairBruteForce(int numPlayersLeft){
        if(numPlayersLeft == 0)
            return true;
        if(numPlayersLeft % 2 == 1)
            return pairPlayerToSitOut(numPlayersLeft); //Sit out the worst player who hasn't already, repeat with better players if pairing fails
        
        LinkedList<Player> sublist = extractSublist(players); //Return a sublist of even length of players with similar scores
        boolean didSublistPairingSucceed = false;
        boolean didRecursivePairingSucceed = false;
        while(!didSublistPairingSucceed || !didRecursivePairingSucceed){
            didSublistPairingSucceed = pairSublist(sublist); //Attempt to pair the sublist
            if(didSublistPairingSucceed)
                didRecursivePairingSucceed = pairBruteForce(numPlayersLeft - sublist.size()); //If succeeds, try to pair the remaining players (not in sublist)
            
            if(!didSublistPairingSucceed || !didRecursivePairingSucceed){ //If fails, scrap pairings, add two players and try again
                boolean areThereTwoPlayers = addTwoPlayersToSublistIfExists(players, sublist);
                if(!areThereTwoPlayers)
                    return false; //If there are not two more players, the pairing fails
            }
        }
        return true;
    }

    private boolean pairPlayerToSitOut(int numPlayersLeft){
        for(int i = players.size() - 1; i >= 0; i--){
            Player p = players.get(i);
            boolean didPairingWork;
            if(!p.hasSatOut()){
                round.addGame(new Game(p, null)); //add a game with Player p sitting out and pair them
                playersPaired.add(p);
                didPairingWork = pairBruteForce(numPlayersLeft - 1); //Attempt to pair other players
                
                if(didPairingWork)
                    return true; //If pairing worked, done
                else{
                    playersPaired.remove(p); //If not, unpair the Player p, remove the game and continue
                    round.removeGame();
                }
            }
        }
        return false;
    }

    private LinkedList<Player> extractSublist(ArrayList<Player> players){
        LinkedList<Player> out = new LinkedList<>();
        for(Player p : players){
            if(playersPaired.contains(p)) continue;
            
            if(out.size() == 0 || out.get(0).getScore() == p.getScore() || out.size() % 2 == 1) 
                out.add(p);
            else break;
        }
        return out;
    }

    private boolean addTwoPlayersToSublistIfExists(ArrayList<Player> players, LinkedList<Player> sublist){
        int numPlayersAdded = 0;
        for(Player p : players){
            if(numPlayersAdded == 2)
                break;
            if(!playersPaired.contains(p) && !sublist.contains(p)){
                numPlayersAdded++;
                sublist.add(p);
            }
        }
        return (numPlayersAdded == 2);
    }

    private boolean pairSublist(LinkedList<Player> sublist){
        if(!hasUnpairedPlayer(sublist))
            return true;
        boolean isPairingFirstPlayer = checkIfPairingFirstPlayer(sublist);
        Player p;
        if(isPairingFirstPlayer)
            p = getFirstUnpairedPlayerInSublist(sublist);
        
        else
            p = getLastUnpairedPlayerInSublist(sublist);
            
        playersPaired.add(p);
           
        boolean didPairingSucceed = pairWithWorstPlayer(sublist, p);
        if(!didPairingSucceed){
            playersPaired.remove(p); //If no pairing works for this player, pairing will fail automatically
        }
        return didPairingSucceed;
    }
    private Player getFirstUnpairedPlayerInSublist(LinkedList<Player> sublist){
        for(Player p : sublist){
            if(!playersPaired.contains(p))
                return p;
        }
        return null;
    }
    private Player getLastUnpairedPlayerInSublist(LinkedList<Player> sublist){
        Iterator<Player> lIterator = sublist.descendingIterator();
        while(lIterator.hasNext()){
            Player p = lIterator.next();
            if(!playersPaired.contains(p))
                return p;
        }
        return null;
    }
    private boolean hasUnpairedPlayer(LinkedList<Player> sublist){
        for(Player p : sublist){
            if(!playersPaired.contains(p))
                return true;
        }
        return false;
    }
    private boolean checkIfPairingFirstPlayer(LinkedList<Player> sublist){
        Player firstPlayer = null;
        Player lastPlayer = null;
        for(Player p : sublist){
            if(playersPaired.contains(p))
                continue;
            if(firstPlayer == null)
                firstPlayer = p;
            else
                lastPlayer = p;
        }
        return firstPlayer.getScore() == lastPlayer.getScore();
    }
    private boolean pairWithWorstPlayer(LinkedList<Player> sublist, Player toPairWith){
        Iterator<Player> lIterator = sublist.descendingIterator();
        boolean didPairingSucceed;
        while(lIterator.hasNext()){
            Player p = lIterator.next();
            if(toPairWith.hasPlayedAgainst(p) || playersPaired.contains(p))
                continue;
            addGame(toPairWith, p);
            playersPaired.add(p);
            didPairingSucceed = pairSublist(sublist);
            if(didPairingSucceed)
                return true;
            playersPaired.remove(p);
            round.removeGame();
        }
        return false;
    }
    private void addGame(Player a, Player b){
        int gamesA = a.getGamesAsBlack();
        int gamesB = b.getGamesAsBlack();
        if(gamesA > gamesB)
            round.addGame(new Game(a,b));
        else
            round.addGame(new Game(b,a));
    }

    public Round getRound() {
        return round;
    }
}
