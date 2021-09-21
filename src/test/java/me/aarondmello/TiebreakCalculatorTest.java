package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.aarondmello.constants.Colour;
import me.aarondmello.constants.TiebreakIndex;
import me.aarondmello.datatypes.PlayerGameSummary;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Tiebreaks;
import me.aarondmello.driver.*;

public class TiebreakCalculatorTest {
    
    static LinkedList<PlayerGameSummary> PlayerGameSummarys = new LinkedList<>();

    static Player p1, p2, p3, p4, p5;
    Tiebreaks tiebreaks;
    @BeforeAll
    public static void init(){
        p1 = Mockito.mock(Player.class);
        p2 = Mockito.mock(Player.class);
        p3 = Mockito.mock(Player.class);
        p4 = Mockito.mock(Player.class);
        p5 = Mockito.mock(Player.class);

        PlayerGameSummarys.add(new PlayerGameSummary(2, p1, Colour.WHITE));
        PlayerGameSummarys.add(new PlayerGameSummary(1, p2, Colour.BLACK));
        PlayerGameSummarys.add(new PlayerGameSummary(0, p3, Colour.BLACK));
        PlayerGameSummarys.add(new PlayerGameSummary(2, p4, Colour.BLACK));
        PlayerGameSummarys.add(new PlayerGameSummary(0, p5, Colour.WHITE));

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
    public void testCalculator(){
        TiebreakCalculator.updateTiebreaks(PlayerGameSummarys, new int[]{TiebreakIndex.BUCHHOLZ_CUT_ONE, 
            TiebreakIndex.BUCHHOLZ, TiebreakIndex.WIN_COUNT,  TiebreakIndex.WIN_COUNT_AS_BLACK, TiebreakIndex.PROGRESSIVE_SCORES}, 
            tiebreaks);
        
        assertAll(
            "Test Calculations:",
            () -> assertEquals(20, tiebreaks.getBuchholzCutOne()),
            () -> assertEquals(21, tiebreaks.getBuchholz()),
            () -> assertEquals(18, tiebreaks.getProgressiveScores()),
            () -> assertEquals(2, tiebreaks.getWinCount()),
            () -> assertEquals(1, tiebreaks.getWinCountAsBlack())
        );
        
    }

}
