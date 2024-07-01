package me.aarondmello;

import me.aarondmello.datatypes.Colour;
import me.aarondmello.datatypes.Game;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.PlayerGameSummary;
import me.aarondmello.driver.AdvancedWeightFunction;
import me.aarondmello.driver.PairingSystem;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdvancedPairingTest {

    private ArrayList<Player> players;

    @Test
    public void pairPlayersDifferentOrgFirstRound() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            int totalRounds = 1;
            int roundNumber = 1;
            initPlayers(4, 2, 4);
            List<Game> r = PairingSystem.pairRound(roundNumber, players, totalRounds, new AdvancedWeightFunction(players, totalRounds - roundNumber + 1));
            System.out.println("Different org first round only");
            printPairing(r);
            assertTrue(PairingAlgorithmTest.checkIfAllPlayersPaired(r, players));
            assertTrue(PairingAlgorithmTest.checkIfAllGamesValid(r));
            assertEquals(numSameOrg(r), 0);
        });
    }

    @Test
    public void pairManyPlayersDifferentOrgFirstRound() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            int totalRounds = 6;
            int roundNumber = 1;
            initPlayers(12, 6, 12);
            List<Game> r = PairingSystem.pairRound(1, players, 6, new AdvancedWeightFunction(players, totalRounds - roundNumber + 1));
            System.out.println("Different org first round even");
            printPairing(r);
            assertTrue(PairingAlgorithmTest.checkIfAllPlayersPaired(r, players));
            assertTrue(PairingAlgorithmTest.checkIfAllGamesValid(r));
            assertEquals(numSameOrg(r), 0);
        });
    }

    @Test
    public void pairManyPlayersDifferentOrgOddFirstRound() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            int totalRounds = 6;
            int roundNumber = 1;
            initPlayers(12, 6, 13);
            List<Game> r = PairingSystem.pairRound(1, players, 6, new AdvancedWeightFunction(players, totalRounds - roundNumber + 1));
            System.out.println("Different org first round odd");
            printPairing(r);
            assertTrue(PairingAlgorithmTest.checkIfAllPlayersPaired(r, players));
            assertTrue(PairingAlgorithmTest.checkIfAllGamesValid(r));
            assertEquals(numSameOrg(r), 0);
        });
    }

    @Test
    public void pairManyPlayersDifferentPriorityWeakPlayers() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            int totalRounds = 4;
            int roundNumber = 4;
            initPlayers(6, 6, 6, 6);
            for (int i = 0; i < 6; i++) {
                Player a = players.get(i);
                Player b = players.get(i + 6);
                Player c = players.get(i + 12);
                Player d = players.get(i + 18);
                a.addPlayerGameSummary(
                        new PlayerGameSummary(2, b, Colour.WHITE),
                        new PlayerGameSummary(2, c, Colour.BLACK),
                        new PlayerGameSummary(2, d, Colour.WHITE)
                );
                b.addPlayerGameSummary(
                        new PlayerGameSummary(0, a, Colour.BLACK),
                        new PlayerGameSummary(2, d, Colour.WHITE),
                        new PlayerGameSummary(2, c, Colour.BLACK)
                );
                c.addPlayerGameSummary(
                        new PlayerGameSummary(2, d, Colour.BLACK),
                        new PlayerGameSummary(0, a, Colour.WHITE),
                        new PlayerGameSummary(0, b, Colour.WHITE)
                );
                d.addPlayerGameSummary(
                        new PlayerGameSummary(0, c, Colour.WHITE),
                        new PlayerGameSummary(0, b, Colour.BLACK),
                        new PlayerGameSummary(0, a, Colour.BLACK)
                );
            }

            List<Game> r = PairingSystem.pairRound(roundNumber, players, totalRounds, new AdvancedWeightFunction(players, totalRounds - roundNumber + 1, 2));
            System.out.println("Different org first round odd");
            printPairing(r);
            assertTrue(PairingAlgorithmTest.checkIfAllPlayersPaired(r, players));
            assertTrue(PairingAlgorithmTest.checkIfAllGamesValid(r));
        });
    }

    private void printPairing(List<Game> r){
        for (Game g:r) {
            System.out.println(g.getWhitePlayer().getID() + g.getWhitePlayer().getOrganization() + " " +
                    g.getBlackPlayer().getID() + g.getBlackPlayer().getOrganization());
        }
    }

    private void initPlayers(int ... ints) {
        players = new ArrayList<>();
        char orgChar = 'a';
        int idIndex = 0;
        for (int i : ints) {
            String orgName = "" + orgChar;
            for (int j = 0; j < i; j++) {
                Player p = new Player(orgName + j, orgName);
                p.setID(idIndex);
                players.add(p);
                idIndex++;
            }
            orgChar++;
        }
    }

    private int numSameOrg(List<Game> r){
        return r.stream().mapToInt(g -> (g.getWhitePlayer().getOrganization()).equals(g.getBlackPlayer().getOrganization())? 1:0).sum();
    }
}
