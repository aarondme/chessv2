package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.*;

import me.aarondmello.constants.Colour;
import me.aarondmello.datatypes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import me.aarondmello.driver.PairingSystem;

public class PairingAlgorithmTest {
    
    PairingSystem pairingSystem;
    ArrayList<Player> players;
    final int WHITE = 8;
    final int BLACK = 0;
    final int WIN = 3;
    final int LOSS = 0;
    final int DRAW = 1;
    final int NOPLAY = -1;

    @BeforeEach
    public void setup(){
        pairingSystem = new PairingSystem();
        players = new ArrayList<>();
    }

    private void initPlayers(int numberOfPlayers, int[][] matches){
        for(int i = 0; i < numberOfPlayers; i++){
            Player p = new Player("" + i, "org");
            p.setID(i);
            players.add(p);
        }
        for(int i = 0; i < numberOfPlayers; i++){
            for(int j = 0; j < numberOfPlayers; j++){
                if(matches[i][j] != NOPLAY) {
                    PlayerGameSummary summary = initializeSummary(matches[i][j], j);
                    players.get(i).addPlayerGameSummary(summary);
                }
            }
        }
    }

    private PlayerGameSummary initializeSummary(int bitmask, int opponent) {
        int color = ((bitmask & WHITE) == 0)? Colour.BLACK:Colour.WHITE;
        int score = ((bitmask & WIN) == 3)? 2: ((bitmask & DRAW) == 1)? 1 : 0;
        Player opp = players.get(opponent);
        return new PlayerGameSummary(score, opp, color);
    }

    @ParameterizedTest
    @ValueSource(ints = {5,6})
    public void pairPlayersFirstRound(int numPlayers){
        int[][] matches = new int[numPlayers][numPlayers];
        for(int i = 0; i < numPlayers; i++)
            for(int j = 0; j < numPlayers; j++)
                matches[i][j] = NOPLAY;
        initPlayers(numPlayers, matches);
        Round r = pairingSystem.pairRound(1, players);
        
        assertTrue(checkIfAllPlayersPaired(r));
    }

    @Test
    public void pairLastOption(){
        int[][] matches = {
                {NOPLAY,WHITE+DRAW , BLACK+DRAW, NOPLAY},
                {BLACK+DRAW, NOPLAY, NOPLAY, WHITE+WIN},
                {WHITE+DRAW ,NOPLAY,NOPLAY, BLACK+DRAW},
                {NOPLAY,BLACK+LOSS ,WHITE+DRAW ,NOPLAY}};
        initPlayers(4, matches);

        Round r = pairingSystem.pairRound(3, players);

        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(checkIfPairingValid(r));
    }

    @Test
    public void pairSecondRound(){
        int[][] matches = {
                {NOPLAY, WHITE+WIN, NOPLAY, NOPLAY, NOPLAY},
                {BLACK+LOSS, NOPLAY, NOPLAY, NOPLAY, NOPLAY},
                {NOPLAY,NOPLAY,NOPLAY,BLACK+WIN,NOPLAY},
                {NOPLAY,NOPLAY,WHITE+LOSS,NOPLAY,NOPLAY},
                {NOPLAY,NOPLAY,NOPLAY,NOPLAY,NOPLAY}
        };
        initPlayers(5, matches);
        players.get(4).addPlayerGameSummary(new PlayerGameSummary(2, NullPlayer.getInstance(), Colour.WHITE));
        Collections.sort(players, Collections.reverseOrder());

        Round r = pairingSystem.pairRound(2, players);

        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(checkIfPairingValid(r));
        assertTrue(scoreDifference(r) <= 2);
    }


    @Test
    public void pairThirdRound(){
        int[][] matches = {
                {NOPLAY, WHITE+WIN, BLACK+WIN, NOPLAY, NOPLAY},
                {BLACK+LOSS, NOPLAY, NOPLAY, NOPLAY, WHITE+LOSS},
                {WHITE+LOSS,NOPLAY,NOPLAY,BLACK+WIN,NOPLAY},
                {NOPLAY,NOPLAY,WHITE+LOSS,NOPLAY,NOPLAY},
                {NOPLAY,BLACK+WIN,NOPLAY,NOPLAY,NOPLAY}
        };
        initPlayers(5, matches);
        Collections.sort(players, Collections.reverseOrder());
        Round r = pairingSystem.pairRound(3, players);


        assertTrue(checkIfAllPlayersPaired(r));
        assertTrue(checkIfPairingValid(r));
        assertTrue(scoreDifference(r) <= 4);
    }

    private int scoreDifference(Round r){
        int diff = 0;
        for(Game g : r.getGames()){
            diff += Math.abs(g.getWhitePlayer().getScore() - g.getBlackPlayer().getScore());
        }
        return diff;
    }

    private boolean checkIfAllPlayersPaired(Round round){
        LinkedList<Game> games = round.getGames();
        HashSet<Integer> uniquePairedPlayers = new HashSet<>();
        boolean containsNullPlayerAsBlack = false;
        for(Game g : games){
            Player black = g.getBlackPlayer();
            uniquePairedPlayers.add(g.getWhitePlayer().getID());
            if(black instanceof NullPlayer)
                containsNullPlayerAsBlack = true;
               
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
