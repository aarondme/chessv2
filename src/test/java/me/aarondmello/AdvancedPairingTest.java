package me.aarondmello;

import me.aarondmello.datatypes.Game;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Round;
import me.aarondmello.driver.AdvancedWeightFunction;
import me.aarondmello.driver.PairingSystem;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class AdvancedPairingTest {

    private ArrayList<Player> players;

    @Test
    public void pairPlayersDifferentOrgFirstRound() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            initPlayers(4, 2, 4);
            Round r = PairingSystem.pairRound(1, players, 1, new AdvancedWeightFunction());
            System.out.println("Different org first round only");
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(PairingAlgorithmTest.checkIfAllPlayersPaired(r, players));
            assertTrue(PairingAlgorithmTest.checkIfAllGamesValid(r));
            assertEquals(numSameOrg(r), 0);
        });
    }

    @Test
    public void pairManyPlayersDifferentOrgFirstRound() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            initPlayers(12, 6, 12);
            Round r = PairingSystem.pairRound(1, players, 6, new AdvancedWeightFunction());
            System.out.println("Different org first round even");
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(PairingAlgorithmTest.checkIfAllPlayersPaired(r, players));
            assertTrue(PairingAlgorithmTest.checkIfAllGamesValid(r));
            assertEquals(numSameOrg(r), 0);
        });
    }

    @Test
    public void pairManyPlayersDifferentOrgOddFirstRound() {
        assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
            initPlayers(12, 6, 13);
            Round r = PairingSystem.pairRound(1, players, 6, new AdvancedWeightFunction());
            System.out.println("Different org first round odd");
            for (Game g:r.getGames()) {
                System.out.println(g.getWhitePlayer().getID() + " " + g.getBlackPlayer().getID());
            }
            assertTrue(PairingAlgorithmTest.checkIfAllPlayersPaired(r, players));
            assertTrue(PairingAlgorithmTest.checkIfAllGamesValid(r));
            assertEquals(numSameOrg(r), 0);
        });
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

    private int numSameOrg(Round r){
        return r.getGames().stream().mapToInt(g -> (g.getWhitePlayer().getOrganization()).equals(g.getBlackPlayer().getOrganization())? 1:0).sum();
    }
}
