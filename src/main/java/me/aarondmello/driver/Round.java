package me.aarondmello.driver;
import java.util.*;
public class Round{
    private LinkedList<Game> games;
    Round(){
        games = new LinkedList<>();
    }
    public void pairFirstRound(ArrayList<Player> players){
        int frontPointer = 0;
        int backPointer = players.size() - 1;
        while(frontPointer < backPointer){
            Player white = players.get(frontPointer);
            Player black = players.get(backPointer);
            games.add(new Game(white, black));
        }
    }
    public LinkedList<Game> getGames() {
        return games;
    }

    public void pairSubsequentRounds(ArrayList<Player> players){
        pairBruteForce(players, players.size());
    }

    private boolean pairBruteForce(ArrayList<Player> players, int numPlayersLeft){
        if(numPlayersLeft == 0)
            return true;
        if(numPlayersLeft % 2 == 1)
            return pairPlayerToSitOut(players, numPlayersLeft);
        
        LinkedList<Player> sublist = extractSublist(players);
        boolean didSublistPairingSucceed = false;
        while(!didSublistPairingSucceed){
            didSublistPairingSucceed = pairSublist(sublist);
            if(!didSublistPairingSucceed){
                boolean areThereTwoPlayers = addTwoPlayersToSublistIfExists(players, sublist);
                if(!areThereTwoPlayers)
                    return false;
            }
            else{
                didSublistPairingSucceed = pairBruteForce(players, numPlayersLeft - sublist.size());
                if(!didSublistPairingSucceed){
                    boolean areThereTwoPlayers = addTwoPlayersToSublistIfExists(players, sublist);
                    if(!areThereTwoPlayers)
                        return false;
                }
            }
        }
        return true;
    }

    private boolean pairPlayerToSitOut(ArrayList<Player> players, int numPlayersLeft){
        for(int i = players.size() - 1; i >= 0; i--){
            Player p = players.get(i);
            boolean didPairingWork;
            if(!p.hasSatOut()){
                games.add(new Game(p, null));
                p.setIsPaired(true);
                didPairingWork = pairBruteForce(players, numPlayersLeft - 1);
                
                if(didPairingWork)
                    return true;
                else{
                    p.setIsPaired(false);
                    games.pollLast(); 
                }
            }
        }
        return false;
    }

    private LinkedList<Player> extractSublist(ArrayList<Player> players){
        LinkedList<Player> out = new LinkedList<>();
        for(Player p : players){
            if(p.isPaired()) continue;
            
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
            if(!p.isPaired() && !sublist.contains(p)){
                numPlayersAdded++;
                sublist.add(p);
            }
        }
        return (numPlayersAdded == 2);
    }

    private boolean pairSublist(LinkedList<Player> players){
        if(hasUnpairedPlayer(players))
            return true;
        boolean isPairingFirstPlayer = checkIfPairingFirstPlayer(players);
        Player p;
        if(isPairingFirstPlayer)
            p = getFirstUnpairedPlayerInSublist(players);
        
        else
            p = getLastUnpairedPlayerInSublist(players);
            
        p.setIsPaired(true);
           
        boolean didPairingSucceed = pairWithWorstPlayer(players, p);
        if(!didPairingSucceed){
            p.setIsPaired(false);
        }
        return didPairingSucceed;
    }
    private Player getFirstUnpairedPlayerInSublist(LinkedList<Player> players){
        for(Player p : players){
            if(!p.isPaired())
                return p;
        }
        return null;
    }
    private Player getLastUnpairedPlayerInSublist(LinkedList<Player> players){
        Iterator<Player> lIterator = players.descendingIterator();
        while(lIterator.hasNext()){
            Player p = lIterator.next();
            if(!p.isPaired())
                return p;
        }
        return null;
    }
    private boolean hasUnpairedPlayer(LinkedList<Player> players){
        for(Player p : players){
            if(!p.isPaired())
                return true;
        }
        return false;
    }
    private boolean checkIfPairingFirstPlayer(LinkedList<Player> players){
        Player firstPlayer = null;
        Player lastPlayer = null;
        for(Player p : players){
            if(firstPlayer == null && !p.isPaired())
                firstPlayer = p;
            if(!p.isPaired())
                lastPlayer = p;
        }
        return firstPlayer.getScore() == lastPlayer.getScore();
    }
    private boolean pairWithWorstPlayer(LinkedList<Player> players, Player toPairWith){
        Iterator<Player> lIterator = players.descendingIterator();
        boolean didPairingSucceed;
        while(lIterator.hasNext()){
            Player p = lIterator.next();
            if(toPairWith.hasPlayedAgainst(p))
                continue;
            addGame(toPairWith, p);
            p.setIsPaired(true);
            didPairingSucceed = pairSublist(players);
            if(didPairingSucceed)
                return true;
            p.setIsPaired(false);
        }
        return false;
    }
    private void addGame(Player a, Player b){
        int gamesA = a.getGamesAsBlack();
        int gamesB = b.getGamesAsBlack();
        if(gamesA > gamesB)
            games.add(new Game(a,b));
        else
            games.add(new Game(b,a));
    }
}