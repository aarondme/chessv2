package me.aarondmello.driver;

import java.util.ArrayList;
import java.util.LinkedList;

import me.aarondmello.datatypes.*;
import me.aarondmello.tiebreaks.TiebreakType;

public class PairingSystem {
    Round round;
    ArrayList<Player> players;

    public PairingSystem(){
    }

    public Round pairRound(int roundNumber, ArrayList<Player> players){
        this.players = players;
        round = new Round();
        if(roundNumber == 1)
            pairFirstRound();
        else
            determineMatches();
        return round;
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
            round.addGame(new Game(players.get(frontPointer), NullPlayer.getInstance()));
    }

    /**
     * Determines the matches
     */
    public boolean determineMatches() {
        int indexOfSittingOut = players.size();
        Game bye = null;

        // Loops until break condition is hit; successful combination found or all
        // combinations tried
        while (true) {
            LinkedList<Player> playersLeft = new LinkedList<Player>(players);
            round = new Round();

            // If odd number of players, this sits out the worst ranked player that hasn't
            // already sat out
            // If this is not the first loop, it sits out the worst player that was not
            // tried that hasn't already sat out
            if (playersLeft.size() % 2 == 1) {
                indexOfSittingOut = findIndexOfSittingOut(indexOfSittingOut, players);
                if(indexOfSittingOut == -1)
                    break;
                Player z = playersLeft.remove(indexOfSittingOut);
                bye = new Game(z, NullPlayer.getInstance());
            }

            // Loops until all players are matched, or until all combinations are tried
            while (playersLeft.size() > 0) {
                ArrayList<Player> sublist = getEvenLengthSublistOfTopPlayers(playersLeft);

                // Loops until a set of matches for the temporary list is found
                while (true) {
                    ArrayList<Game> m = pairSublist(sublist);
                    //If the sublist was paired, add them all to the list.
                    if(m != null){
                        round.addAllGames(m);
                        break;
                    }

                    // If no match is found, tries to add more players to the temporary list
                    // If there are 2 or more players left, add them and try again
                    if (playersLeft.size() >= 2) {
                        sublist.add(playersLeft.pollFirst());
                        sublist.add(playersLeft.pollFirst());
                        continue;
                    }
                    // If all but one, or all players are already on the list, break
                    if (sublist.size() >= players.size() - 1)
                        break;

                    // Remove the match with the weakest players, and add the players involved to
                    // the temporary list and try again
                    Game q = round.removeGame();
                    Player a = q.getWhitePlayer();
                    Player b = q.getBlackPlayer();
                    if (a == NullPlayer.getInstance())
                        break;

                    sublist.add(a);
                    sublist.add(b);
                }
            }
            // If this is true, all players are paired, so break
            if (round.getNumberOfGames() == players.size() / 2)
                break;
            // If there are an even number of players, all combinations were tried in the
            // worst case, so break from the loop
            if (players.size() % 2 == 0)
                break;
        }
        // If this is true, not all players were paired
        if (round.getNumberOfGames() != players.size() / 2)
            return false;
        // Add the odd player's match
        if (bye != null)
            round.addGame(bye);
        return true;
    }

    private ArrayList<Player> getEvenLengthSublistOfTopPlayers(LinkedList<Player> playersLeft) {
        ArrayList<Player> temp = new ArrayList<Player>();
        int score = playersLeft.get(0).getScore();

        while (playersLeft.size() > 1 && playersLeft.get(0).getScore() == score) {
            temp.add(playersLeft.pollFirst());
            temp.add(playersLeft.pollFirst());
        }

        return temp;
    }

    private int findIndexOfSittingOut(int maxIndex, ArrayList<Player> p) {
        for (int i = maxIndex - 1; i >= 0; i--) {
            if (!p.get(i).hasSatOut())
                return i;
        }
        return -1;
    }



    private ArrayList<Game> pairSublist(ArrayList<Player> sub) {
        if (sub.size() == 0)
            return new ArrayList<>();
        // True if the first and last player's score are the same
        boolean isPairingFirstPlayer = (sub.get(0).getScore() == sub.get(sub.size() - 1).getScore());
        // Loops through all players on the list to partner with player p
        for (int i = sub.size() - 2; i >= 0; i--) {
            ArrayList<Player> temp = new ArrayList<Player>(sub);

            Player p = temp.remove((isPairingFirstPlayer) ? 0 : sub.size() - 1);

            Player q = temp.remove(i);

            if(p.hasPlayedAgainst(q))
                continue;

            ArrayList<Game> mat = pairSublist(temp);
            if (mat != null){
                Game g = pairPlayers(isPairingFirstPlayer, p, q);
                mat.add(g);
                return mat;
            }

        }
        return null;
    }

    private Game pairPlayers(boolean isPairingFirstPlayer, Player p, Player q) {
        if (p.getGamesAsBlack() > q.getGamesAsBlack()) {
            return new Game(p, q);
        } else if (p.getGamesAsBlack() < q.getGamesAsBlack()) {
            return new Game(q, p);
        } else if (isPairingFirstPlayer) {
            return new Game(p, q);
        } else {
            return new Game(q, p);
        }
    }
}
