package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.aarondmello.driver.*;

public class TiebreakCalculatorTest {
    
    static LinkedList<GameResult> gameResults = new LinkedList<>();

    static Player p1, p2, p3, p4, p5;
    Tiebreaks tiebreaks;
    @BeforeAll
    public static void init(){
        p1 = Mockito.mock(Player.class);
        p2 = Mockito.mock(Player.class);
        p3 = Mockito.mock(Player.class);
        p4 = Mockito.mock(Player.class);
        p5 = Mockito.mock(Player.class);

        gameResults.add(new GameResult(2, p1, Colour.WHITE));
        gameResults.add(new GameResult(1, p2, Colour.BLACK));
        gameResults.add(new GameResult(0, p3, Colour.BLACK));
        gameResults.add(new GameResult(2, p4, Colour.BLACK));
        gameResults.add(new GameResult(0, p5, Colour.WHITE));

        when(p1.getScore()).thenReturn(7);
        when(p2.getScore()).thenReturn(4);
        when(p3.getScore()).thenReturn(3);
        when(p4.getScore()).thenReturn(1);
        when(p5.getScore()).thenReturn(6);
    }
    
    @BeforeEach
    public void setup(){
        tiebreaks = new Tiebreaks();
    }

    @Test
    public void testBuchholzCutOne(){
        TiebreakCalculator.updateTiebreaks(gameResults, new int[]{TiebreakIndex.BUCHOLZ_CUT_ONE}, tiebreaks);
        
        assertEquals(20, tiebreaks.getBuchholzCutOne());
    }

    @Test
    public void testBuchholz(){
        TiebreakCalculator.updateTiebreaks(gameResults, new int[]{TiebreakIndex.BUCHOLZ}, tiebreaks);

        assertEquals(21, tiebreaks.getBuchholz());
    }

    @Test
    public void testProgressiveScores(){
        TiebreakCalculator.updateTiebreaks(gameResults, new int[]{TiebreakIndex.PROGRESSIVE_SCORES}, tiebreaks);

        assertEquals(18, tiebreaks.getProgressiveScores());
    }

    @Test
    public void testWinCount(){
        TiebreakCalculator.updateTiebreaks(gameResults, new int[]{TiebreakIndex.WIN_COUNT}, tiebreaks);

        assertEquals(2, tiebreaks.getWinCount());
    }

    @Test
    public void testWinCountAsBlack(){
        TiebreakCalculator.updateTiebreaks(gameResults, new int[]{TiebreakIndex.WIN_COUNT_AS_BLACK}, tiebreaks);

        assertEquals(1, tiebreaks.getWinCountAsBlack());
    }
}
