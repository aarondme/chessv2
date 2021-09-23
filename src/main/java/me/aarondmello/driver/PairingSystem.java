package me.aarondmello.driver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import me.aarondmello.datatypes.Game;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Round;

public class PairingSystem {
    Round round;
    HashSet<Player> playersPaired;

    public PairingSystem(Round round){
        this.round = round;
        playersPaired = new HashSet<Player>();
    }

    /**
     * Populates the round with the proper games. 
     * @param players the list of players, expected to be sorted from highest to lowest
     * @param roundNumber the round number
     */
    public void pairRound(ArrayList<Player> players, int roundNumber){
        if(roundNumber == 1)
            pairFirstRound(players);
        else
            pairSubsequentRounds(players);
    }

    private void pairFirstRound(ArrayList<Player> players){
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

    private void pairSubsequentRounds(ArrayList<Player> players){
        pairBruteForce(players, players.size());
    }

    private boolean pairBruteForce(ArrayList<Player> players, int numPlayersLeft){
        if(numPlayersLeft == 0)
            return true;
        if(numPlayersLeft % 2 == 1)
            return pairPlayerToSitOut(players, numPlayersLeft); //Sit out the worst player who hasn't already, repeat with better players if pairing fails
        
        LinkedList<Player> sublist = extractSublist(players); //Return a sublist of even length of players with similar scores
        boolean didSublistPairingSucceed = false;
        boolean didRecursivePairingSucceed = false;
        while(!didSublistPairingSucceed || !didRecursivePairingSucceed){
            didSublistPairingSucceed = pairSublist(sublist); //Attempt to pair the sublist
            if(didSublistPairingSucceed)
                didRecursivePairingSucceed = pairBruteForce(players, numPlayersLeft - sublist.size()); //If succeeds, try to pair the remaining players (not in sublist)
            
            if(!didSublistPairingSucceed || !didRecursivePairingSucceed){ //If fails, scrap pairings, add two players and try again
                boolean areThereTwoPlayers = addTwoPlayersToSublistIfExists(players, sublist);
                if(!areThereTwoPlayers)
                    return false; //If there are not two more players, the pairing fails
            }
        }
        return true;
    }

    private boolean pairPlayerToSitOut(ArrayList<Player> players, int numPlayersLeft){
        for(int i = players.size() - 1; i >= 0; i--){
            Player p = players.get(i);
            boolean didPairingWork;
            if(!p.hasSatOut()){
                round.addGame(new Game(p, null)); //add a game with Player p sitting out and pair them
                playersPaired.add(p);
                didPairingWork = pairBruteForce(players, numPlayersLeft - 1); //Attempt to pair other players
                
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

    private boolean pairSublist(LinkedList<Player> players){
        if(!hasUnpairedPlayer(players))
            return true;
        boolean isPairingFirstPlayer = checkIfPairingFirstPlayer(players);
        Player p;
        if(isPairingFirstPlayer)
            p = getFirstUnpairedPlayerInSublist(players);
        
        else
            p = getLastUnpairedPlayerInSublist(players);
            
        playersPaired.add(p);
           
        boolean didPairingSucceed = pairWithWorstPlayer(players, p);
        if(!didPairingSucceed){
            playersPaired.remove(p); //If no pairing works for this player, pairing will fail automatically
        }
        return didPairingSucceed;
    }
    private Player getFirstUnpairedPlayerInSublist(LinkedList<Player> players){
        for(Player p : players){
            if(!playersPaired.contains(p))
                return p;
        }
        return null;
    }
    private Player getLastUnpairedPlayerInSublist(LinkedList<Player> players){
        Iterator<Player> lIterator = players.descendingIterator();
        while(lIterator.hasNext()){
            Player p = lIterator.next();
            if(!playersPaired.contains(p))
                return p;
        }
        return null;
    }
    private boolean hasUnpairedPlayer(LinkedList<Player> players){
        for(Player p : players){
            if(!playersPaired.contains(p))
                return true;
        }
        return false;
    }
    private boolean checkIfPairingFirstPlayer(LinkedList<Player> players){
        Player firstPlayer = null;
        Player lastPlayer = null;
        for(Player p : players){
            if(playersPaired.contains(p))
                continue;
            if(firstPlayer == null)
                firstPlayer = p;
            else
                lastPlayer = p;
        }
        return firstPlayer.getScore() == lastPlayer.getScore();
    }
    private boolean pairWithWorstPlayer(LinkedList<Player> players, Player toPairWith){
        Iterator<Player> lIterator = players.descendingIterator();
        boolean didPairingSucceed;
        while(lIterator.hasNext()){
            Player p = lIterator.next();
            if(toPairWith.hasPlayedAgainst(p) || playersPaired.contains(p))
                continue;
            addGame(toPairWith, p);
            playersPaired.add(p);
            didPairingSucceed = pairSublist(players);
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
