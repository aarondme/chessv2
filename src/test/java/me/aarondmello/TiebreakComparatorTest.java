package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.RepeatedTest;

import me.aarondmello.driver.*;

import java.util.*;
import java.util.function.*;

public class TiebreakComparatorTest {
    ArrayList<Tiebreaks> tiebreaks;
    private static HashMap<Integer, BiConsumer<Tiebreaks, Integer>> keyToTiebreak = new HashMap<>(){{
        put(TiebreakIndex.BUCHHOLZ, (Tiebreaks t, Integer i) -> {t.setBuchholz(i);});
        put(TiebreakIndex.BUCHHOLZ_CUT_ONE, (Tiebreaks t, Integer i) -> {t.setBuchholzCutOne(i);});
        put(TiebreakIndex.SONNEBORN_BERGER, (Tiebreaks t, Integer i) -> {t.setSonnebornBerger(i);});
        put(TiebreakIndex.PROGRESSIVE_SCORES, (Tiebreaks t, Integer i) -> {t.setProgressiveScores(i);});
        put(TiebreakIndex.DIRECT_ENCOUNTER, (Tiebreaks t, Integer i) -> {t.setDirectEncounter(i);});
        put(TiebreakIndex.WIN_COUNT, (Tiebreaks t, Integer i) -> {t.setWinCount(i);});
        put(TiebreakIndex.WIN_COUNT_AS_BLACK, (Tiebreaks t, Integer i)-> {t.setWinCountAsBlack(i);});
    }};
    
    @RepeatedTest(5)
    public void noRatingSoloTiebreakTest(){
        initNoRatingSoloTiebreakTest();
        ArrayList<Tiebreaks> expected = new ArrayList<>(tiebreaks);
        TiebreaksComparator comparator = new TiebreaksComparator(TiebreakConfig.NO_RATING_SOLO);
        Collections.shuffle(tiebreaks);

        Collections.sort(tiebreaks, comparator); //expected to sort in descending order.
        
        assertIterableEquals(expected, tiebreaks);
    }
    public void initNoRatingSoloTiebreakTest(){
        int[][] tiebreakValues = {
            {1,1,1,1,1,0,0,0}, //0th tiebreak
            {0,0,0,0,0,1,0,0},
            {0,0,0,0,0,0,1,0},
            {1,1,1,1,0,0,0,0},
            {1,1,1,0,1,1,1,1},
            {1,1,0,0,0,1,1,0},
            {1,0,1,1,1,1,1,1}}; //7th tiebreak
        int[] tiebreaksToUse = TiebreakConfig.NO_RATING_SOLO;
        initTiebreaks(tiebreakValues, tiebreaksToUse);    
    }

    public void initTiebreaks(int[][] tiebreakValues, int[] tiebreaksToUse){
        int numberOfTiebreakObjects = tiebreakValues[0].length;
        int numberOfTiebreaksMethods = tiebreakValues.length;
        
        tiebreaks = new ArrayList<Tiebreaks>();
        for(int i = 0; i < numberOfTiebreakObjects; i++)
            tiebreaks.add(new Tiebreaks());
        
        for(int i = 0; i < numberOfTiebreakObjects; i++)
            for(int j = 0; j < numberOfTiebreaksMethods; j++){
                BiConsumer<Tiebreaks,Integer> method = keyToTiebreak.get(tiebreaksToUse[j]);
                method.accept(tiebreaks.get(i),tiebreakValues[j][i]);
            }
    }


}
