package me.aarondmello;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import me.aarondmello.constants.Colour;
import me.aarondmello.datatypes.*;
import me.aarondmello.tiebreaks.Tiebreak;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import me.aarondmello.driver.PairingSystem;

public class PairingAlgorithmTest {
    PairingSystem pairingSystem;
    ArrayList<Player> players;

    @BeforeEach
    public void setup(){
        pairingSystem = new PairingSystem();
        players = new ArrayList<>();
    }

    private void initPlayers(int numberOfPlayers){
        for(int i = 0; i < numberOfPlayers; i++){
            Player p = new Player("" + i, "org");
            p.setID(i);
            players.add(p);
        }
    }


    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5,6})
    public void pairPlayersFirstRound(int numPlayers){
        initPlayers(numPlayers);
        Round r = pairingSystem.pairRound(1, players, 3);
        for (Game g:r.getGames()) {
            System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
        }
        assertTrue(checkIfAllPlayersPaired(r));
    }


    @Test
    public void stressTest() {
        assertTimeoutPreemptively(Duration.ofSeconds(60), () -> {
            initPlayers(30);
            Round r = pairingSystem.pairRound(1, players, 6);
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(checkIfAllPlayersPaired(r));
        });
    }

    @Test
    public void pairLastOption(){
        initPlayers(4);
        players.get(0).addPlayerGameSummary(
                new PlayerGameSummary(1, players.get(1), Colour.WHITE),
                new PlayerGameSummary(1, players.get(2), Colour.BLACK)
        );
        players.get(1).addPlayerGameSummary(
                new PlayerGameSummary(1, players.get(0), Colour.BLACK),
                new PlayerGameSummary(2, players.get(3), Colour.WHITE)
        );
        players.get(2).addPlayerGameSummary(
                new PlayerGameSummary(1, players.get(3), Colour.BLACK),
                new PlayerGameSummary(1, players.get(0), Colour.WHITE)
        );
        players.get(3).addPlayerGameSummary(
                new PlayerGameSummary(1, players.get(2), Colour.WHITE),
                new PlayerGameSummary(0, players.get(1), Colour.BLACK)
        );


        Round r = pairingSystem.pairRound(3, players, 3);
        for (Game g : r.getGames()) {
            System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
        }
        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(checkIfPairingValid(r));
    }

    @Test
    public void pairSecondRound(){
        initPlayers(5);
        players.get(0).addPlayerGameSummary(
                new PlayerGameSummary(2, players.get(1), Colour.WHITE)
        );
        players.get(1).addPlayerGameSummary(
                new PlayerGameSummary(0, players.get(0), Colour.BLACK)
        );
        players.get(2).addPlayerGameSummary(
                new PlayerGameSummary(2, players.get(3), Colour.BLACK)
        );
        players.get(3).addPlayerGameSummary(
                new PlayerGameSummary(0, players.get(2), Colour.WHITE)
        );
        players.get(4).addPlayerGameSummary(
                new PlayerGameSummary(2, NullPlayer.getInstance(), Colour.WHITE)
        );


        Round r = pairingSystem.pairRound(2, players, 4);
        for (Game g : r.getGames()) {
            System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
        }
        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(checkIfPairingValid(r));
    }


    @Test
    public void pairThirdRound(){
        initPlayers(5);
        players.get(0).addPlayerGameSummary(
                new PlayerGameSummary(2, players.get(1), Colour.WHITE),
                new PlayerGameSummary(2, players.get(2), Colour.BLACK)
        );
        players.get(1).addPlayerGameSummary(
                new PlayerGameSummary(0, players.get(0), Colour.BLACK),
                new PlayerGameSummary(0, players.get(4), Colour.WHITE)
        );
        players.get(2).addPlayerGameSummary(
                new PlayerGameSummary(2, players.get(3), Colour.BLACK),
                new PlayerGameSummary(0, players.get(0), Colour.WHITE)
        );
        players.get(3).addPlayerGameSummary(
                new PlayerGameSummary(0, players.get(2), Colour.WHITE),
                new PlayerGameSummary(2, NullPlayer.getInstance(), Colour.WHITE)
        );
        players.get(4).addPlayerGameSummary(
                new PlayerGameSummary(2, NullPlayer.getInstance(), Colour.WHITE),
                new PlayerGameSummary(2, players.get(1), Colour.BLACK)
        );
        Round r = pairingSystem.pairRound(3, players, 4);
        for (Game g : r.getGames()) {
            System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
        }
        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(checkIfPairingValid(r));
    }

    private boolean checkIfAllPlayersPaired(Round round){
        LinkedList<Game> games = round.getGames();
        HashSet<Integer> uniquePairedPlayers = new HashSet<>();
        boolean nullPlayerPaired = false;
        for(Game g : games){
            Player black = g.getBlackPlayer();
            uniquePairedPlayers.add(g.getWhitePlayer().getID());
            if(black instanceof NullPlayer)
                nullPlayerPaired = true;
               
            uniquePairedPlayers.add(black.getID());
                
        }
        boolean oddNumberOfPlayers = players.size() % 2 == 1;
        //An additional "null" player expected if odd
        int numberOfUniquePlayersExpected = players.size() + ((oddNumberOfPlayers)? 1:0);
        int numberOfGamesExpected = numberOfUniquePlayersExpected/2;
        boolean areCorrectNumberOfGames = games.size() == numberOfGamesExpected;  
        boolean allPlayersPaired = numberOfUniquePlayersExpected == uniquePairedPlayers.size();

        return allPlayersPaired && (!oddNumberOfPlayers ^ nullPlayerPaired) && areCorrectNumberOfGames;
    } 
    private boolean checkIfPairingValid(Round round){
        LinkedList<Game> games = round.getGames();
        for(Game g : games){
            if(!checkIfGameValid(g))
                return false;
        }
        return true;
    }

    private boolean checkIfGameValid(Game g){
        Player white = g.getWhitePlayer();
        Player black = g.getBlackPlayer();
        
        return !white.hasPlayedAgainst(black) && !black.hasPlayedAgainst(white); 
    }
}
