package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import me.aarondmello.datatypes.Game;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Round;
import me.aarondmello.driver.PairingSystem;

public class PairingAlgorithmTest {
    
    Round round;
    PairingSystem pairingSystem;
    ArrayList<Player> players;

    @BeforeEach
    public void setup(){
        round = new Round();
        pairingSystem = new PairingSystem(round);
        players = new ArrayList<>();
    }

    private void initEmptyPlayerMocks(int numberOfPlayers){
        for(int i = 0; i < numberOfPlayers; i++){
            players.add(Mockito.mock(Player.class));
            when(players.get(i).getID()).thenReturn(i);
        }
    }
    private void initPlayerMockOpponents(boolean[][] opponents){
        for(int i = 0; i < players.size(); i++)
            for(int j = 0; j < players.size(); j++)
                when(players.get(i).hasPlayedAgainst(players.get(j))).thenReturn(opponents[i][j]);
    }
    private void initPlayerMockScores(int[] scores){
        for(int i = 0; i < players.size(); i++)
            when(players.get(i).getScore()).thenReturn(scores[i]);
    }

    @ParameterizedTest
    @ValueSource(ints = {5,6})
    public void pairPlayersFirstRound(int numPlayers){
        initEmptyPlayerMocks(numPlayers);
        pairingSystem.setPlayers(players);
        pairingSystem.pairRound(1);
        
        assertTrue(checkIfAllPlayersPaired());
    }

    @Test
    public void pairLastOption(){
        initEmptyPlayerMocks(4);
        boolean[][] opponentMatrix = 
        {{false,true ,true ,false},
         {true ,false,false,true },
         {true ,false,false,true },
         {false,true ,true ,false}};
        int[] playerScores = {3,3,2,0};

        initPlayerMockOpponents(opponentMatrix);
        initPlayerMockScores(playerScores);
        pairingSystem.setPlayers(players);


        pairingSystem.pairRound(3);

        assertTrue(checkIfAllPlayersPaired());
        assertTrue(checkIfPairingValid());
    }

    private boolean checkIfAllPlayersPaired(){
        LinkedList<Game> games = round.getGames();
        HashSet<Integer> uniquePairedPlayers = new HashSet<>();
        boolean containsNullPlayerAsBlack = false;
        for(Game g : games){
            Player black = g.getBlackPlayer();
            uniquePairedPlayers.add(g.getWhitePlayer().getID());
            if(black == null){
                containsNullPlayerAsBlack = true;
                uniquePairedPlayers.add(-1);
            }
            else
                uniquePairedPlayers.add(black.getID());
                
        }
        boolean oddNumberOfPlayers = players.size() % 2 == 1;
        //An additional "null" player expected if odd
        int numberOfUniquePlayersExpected = players.size() + ((oddNumberOfPlayers)? 1:0);
        int numberOfGamesExpected = numberOfUniquePlayersExpected/2;
        boolean areCorrectNumberOfGames = games.size() == numberOfGamesExpected;  
        boolean allPlayersPaired = numberOfUniquePlayersExpected == uniquePairedPlayers.size(); 
        boolean nullPlayerPaired = containsNullPlayerAsBlack;  

        return allPlayersPaired && (!oddNumberOfPlayers ^ nullPlayerPaired) && areCorrectNumberOfGames;
    } 
    private boolean checkIfPairingValid(){
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
        if(black == null)
            return !white.hasSatOut();
        else
            return !white.hasPlayedAgainst(black) && !black.hasPlayedAgainst(white); 
    }
}