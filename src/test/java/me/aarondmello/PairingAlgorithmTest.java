package me.aarondmello;

import me.aarondmello.constants.Colour;
import me.aarondmello.datatypes.*;
import me.aarondmello.driver.PairingSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Round r = pairingSystem.pairRound(1, players, Math.min(3, numPlayers-1));
        System.out.println("Pair players first round: " + numPlayers);
        for (Game g:r.getGames()) {
            System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
        }
        assertTrue(checkIfAllPlayersPaired(r));
    }


    @Test
    public void manyPlayersEvenFirstRound() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            initPlayers(30);
            Round r = pairingSystem.pairRound(1, players, 6);
            System.out.println("Many players Even first round");
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(checkIfAllPlayersPaired(r));
            assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
        });
    }

    @Test
    public void manyPlayersEvenSecondRound() {
        initPlayers(30);
        for (int i = 0; i < 30; i += 2) {
            players.get(i).addPlayerGameSummary(new PlayerGameSummary(2, players.get(i + 1), Colour.WHITE));
            players.get(i + 1).addPlayerGameSummary(new PlayerGameSummary(0, players.get(i), Colour.BLACK));
        }
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            Round r = pairingSystem.pairRound(2, players, 6);
            System.out.println("Many players Even second round");
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(checkIfAllPlayersPaired(r));
            assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
        });
    }

    @Test
    public void manyPlayersEvenThirdRound() {
        initPlayers(30);
        for (int i = 0; i < 30; i += 2) {
            players.get(i).addPlayerGameSummary(new PlayerGameSummary(2, players.get(i + 1), Colour.WHITE));
            players.get(i + 1).addPlayerGameSummary(new PlayerGameSummary(0, players.get(i), Colour.BLACK));
        }
        for (int i = 0; i < 28; i+= 4) {
            players.get(i).addPlayerGameSummary(new PlayerGameSummary(2, players.get(i + 1), Colour.WHITE));
            players.get(i + 2).addPlayerGameSummary(new PlayerGameSummary(0, players.get(i), Colour.BLACK));
            players.get(i + 1).addPlayerGameSummary(new PlayerGameSummary(2, players.get(i + 1), Colour.WHITE));
            players.get(i + 3).addPlayerGameSummary(new PlayerGameSummary(0, players.get(i), Colour.BLACK));
        }
        players.get(28).addPlayerGameSummary(new PlayerGameSummary(2, players.get(29), Colour.WHITE));
        players.get(29).addPlayerGameSummary(new PlayerGameSummary(0, players.get(28), Colour.BLACK));

        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            Round r = pairingSystem.pairRound(3, players, 6);
            System.out.println("Many players Even third round");
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(checkIfAllPlayersPaired(r));
            assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
        });
    }


    @Test
    public void manyPlayersOddFirstRound() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            initPlayers(31);
            Round r = pairingSystem.pairRound(1, players, 6);
            System.out.println("Many players Odd first round");
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(checkIfAllPlayersPaired(r));
            assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
        });
    }

    @Test
    public void manyPlayersOddSecondRound() {
        initPlayers(31);
        for (int i = 0; i < 30; i += 2) {
            players.get(i).addPlayerGameSummary(new PlayerGameSummary(2, players.get(i + 1), Colour.WHITE));
            players.get(i + 1).addPlayerGameSummary(new PlayerGameSummary(0, players.get(i), Colour.BLACK));
        }
        players.get(30).addPlayerGameSummary(new PlayerGameSummary(2, NullPlayer.getInstance(), Colour.WHITE));

        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            Round r = pairingSystem.pairRound(2, players, 6);
            System.out.println("Many players Even second round");
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(checkIfAllPlayersPaired(r));
            assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
        });
    }

    @Test
    public void manyPlayersOddThirdRound() {
        initPlayers(31);
        for (int i = 0; i < 30; i += 2) {
            players.get(i).addPlayerGameSummary(new PlayerGameSummary(2, players.get(i + 1), Colour.WHITE));
            players.get(i + 1).addPlayerGameSummary(new PlayerGameSummary(0, players.get(i), Colour.BLACK));
        }
        players.get(30).addPlayerGameSummary(new PlayerGameSummary(2, NullPlayer.getInstance(), Colour.WHITE));
        for (int i = 0; i < 28; i+= 4) {
            players.get(i).addPlayerGameSummary(new PlayerGameSummary(2, players.get(i + 1), Colour.WHITE));
            players.get(i + 2).addPlayerGameSummary(new PlayerGameSummary(0, players.get(i), Colour.BLACK));
            players.get(i + 1).addPlayerGameSummary(new PlayerGameSummary(2, players.get(i + 1), Colour.WHITE));
            players.get(i + 3).addPlayerGameSummary(new PlayerGameSummary(0, players.get(i), Colour.BLACK));
        }
        players.get(28).addPlayerGameSummary(new PlayerGameSummary(2, players.get(30), Colour.WHITE));
        players.get(30).addPlayerGameSummary(new PlayerGameSummary(0, players.get(28), Colour.BLACK));
        players.get(29).addPlayerGameSummary(new PlayerGameSummary(2, NullPlayer.getInstance(), Colour.WHITE));

        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            Round r = pairingSystem.pairRound(3, players, 6);
            System.out.println("Many players Odd third round");
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(checkIfAllPlayersPaired(r));
            assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
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
        System.out.println("Pair last option");
        for (Game g : r.getGames()) {
            System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
        }
        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
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
        System.out.println("Pair second round");
        for (Game g : r.getGames()) {
            System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
        }
        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
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
        System.out.println("Pair third round");
        for (Game g : r.getGames()) {
            System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
        }
        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
    }

    @Test
    public void pairLookingAhead(){
        initPlayers(6);
        players.get(0).addPlayerGameSummary(
                new PlayerGameSummary(2, players.get(4), Colour.WHITE),
                new PlayerGameSummary(2, players.get(5), Colour.BLACK)
        );
        players.get(1).addPlayerGameSummary(
                new PlayerGameSummary(2, players.get(5), Colour.WHITE),
                new PlayerGameSummary(0, players.get(3), Colour.BLACK)
        );
        players.get(2).addPlayerGameSummary(
                new PlayerGameSummary(0, players.get(3), Colour.WHITE),
                new PlayerGameSummary(0, players.get(4), Colour.BLACK)
        );
        players.get(3).addPlayerGameSummary(
                new PlayerGameSummary(2, players.get(2), Colour.BLACK),
                new PlayerGameSummary(2, players.get(1), Colour.WHITE)
        );
        players.get(4).addPlayerGameSummary(
                new PlayerGameSummary(0, players.get(0), Colour.BLACK),
                new PlayerGameSummary(2, players.get(2), Colour.WHITE)
        );
        players.get(5).addPlayerGameSummary(
                new PlayerGameSummary(0, players.get(1), Colour.BLACK),
                new PlayerGameSummary(0, players.get(0), Colour.WHITE)
        );

        Round r = pairingSystem.pairRound(3, players, 4);
        System.out.println("Pair looking ahead");
        for (Game g : r.getGames()) {
            System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
        }
        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(r.getGames().stream().allMatch(this::checkIfGameValid));
        assertTrue(r.getGames().stream().anyMatch(g -> g.getBlackPlayer().getScore() != g.getWhitePlayer().getScore())); //Otherwise, the fourth round necessarily has two players sit out.
    }
    private boolean checkIfAllPlayersPaired(Round round){
        LinkedList<Game> games = round.getGames();
        if(games.size() != (players.size() + 1)/2)
            return false;
        HashSet<Integer> uniquePairedPlayers = new HashSet<>();

        for(Game g : games){
            if(!uniquePairedPlayers.add(g.getWhitePlayer().getID()) ||
                    !uniquePairedPlayers.add(g.getBlackPlayer().getID()))
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
