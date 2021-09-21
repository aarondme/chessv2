package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.aarondmello.datatypes.Game;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Round;

public class PairingAlgorithmTest {
    /** 
    @Test
    public void pairEvenNumberOfPlayersFirstRound(){
        Round round = new Round();
        ArrayList<Player> players = new ArrayList<>();
        for(int i = 0; i < 6; i++)
            players.add(Mockito.mock(Player.class));

        round.pairFirstRound(players);
        
        LinkedList<Game> games = round.getGames();
        HashSet<Player> uniquePairedPlayers = new HashSet<>();
        for(Game g : games){
            uniquePairedPlayers.add(g.getWhitePlayer());
            uniquePairedPlayers.add(g.getBlackPlayer());
        }
        assertEquals(6, uniquePairedPlayers.size());
    }
    */
}
