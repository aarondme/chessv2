package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.aarondmello.datatypes.Player;
import me.aarondmello.tiebreaks.Tiebreak;

public class PlayerSortingTest {
    ArrayList<Player> players = new ArrayList<Player>();
    Player player0 = new Player("a", "b");
    Player player1 = new Player("a", "b");
    Tiebreak tiebreakReturns1 = Mockito.mock(Tiebreak.class);
    Tiebreak tiebreakReturns2 = Mockito.mock(Tiebreak.class);
    
    public void init(){
        players.add(player0);
        players.add(player1);  
        when(tiebreakReturns1.getScore()).thenReturn(1);
        when(tiebreakReturns2.getScore()).thenReturn(2);
    }

    @Test
    public void twoPlayersDifferentScoreNoTiebreaks(){
        init();
        player0.setScore(1);
        player1.setScore(3);

        Collections.sort(players, Collections.reverseOrder());

        assertEquals(player1, players.get(0));
        assertEquals(player0, players.get(1));
    }

    @Test
    public void twoPlayersSameScoreOneDifferentTiebreak(){
        init();
        player0.setScore(1);
        player0.addTiebreak(tiebreakReturns2);
        player1.setScore(1);
        player1.addTiebreak(tiebreakReturns1);

        Collections.sort(players, Collections.reverseOrder());

        assertEquals(player0, players.get(0));
        assertEquals(player1, players.get(1));
    }

    @Test
    public void twoPlayersSameScoreOneMatchingTiebreakOneDifferentTiebreak(){
        init();
        player0.setScore(1);
        player0.addTiebreak(tiebreakReturns2);
        player0.addTiebreak(tiebreakReturns1);
        player1.setScore(1);
        player1.addTiebreak(tiebreakReturns2);
        player1.addTiebreak(tiebreakReturns2);

        Collections.sort(players, Collections.reverseOrder());

        assertEquals(player1, players.get(0));
        assertEquals(player0, players.get(1));
    }
}
