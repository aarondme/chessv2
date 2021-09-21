package me.aarondmello.driver;

import java.util.Comparator;
import java.util.HashMap;
import java.util.function.ToIntBiFunction;

import me.aarondmello.constants.TiebreakIndex;
import me.aarondmello.datatypes.Tiebreaks;

public class TiebreaksComparator implements Comparator<Tiebreaks> {
    private static HashMap<Integer, ToIntBiFunction<Tiebreaks, Tiebreaks>> comparisonMap = new HashMap<>(){{
        put(TiebreakIndex.BUCHHOLZ, (Tiebreaks q, Tiebreaks t) -> {return t.getBuchholz() - q.getBuchholz();});
        put(TiebreakIndex.BUCHHOLZ_CUT_ONE, (Tiebreaks q, Tiebreaks t) -> {return t.getBuchholzCutOne() - q.getBuchholzCutOne();});
        put(TiebreakIndex.SONNEBORN_BERGER, (Tiebreaks q, Tiebreaks t) -> {return t.getSonnebornBerger() - q.getSonnebornBerger();});
        put(TiebreakIndex.PROGRESSIVE_SCORES, (Tiebreaks q, Tiebreaks t) -> {return t.getProgressiveScores() - q.getProgressiveScores();});
        put(TiebreakIndex.DIRECT_ENCOUNTER, (Tiebreaks q, Tiebreaks t) -> {return t.getDirectEncounter() - q.getDirectEncounter();});
        put(TiebreakIndex.WIN_COUNT, (Tiebreaks q, Tiebreaks t) -> {return t.getWinCount() - q.getWinCount();});
        put(TiebreakIndex.WIN_COUNT_AS_BLACK, (Tiebreaks q, Tiebreaks t)-> {return t.getWinCountAsBlack() - q.getWinCountAsBlack();});
    }};
    private int[] compareOrder;
    public TiebreaksComparator(int[] compareOrder){
        this.compareOrder = compareOrder;
    }
    @Override
    public int compare(Tiebreaks o1, Tiebreaks o2) {
        for(int i : compareOrder){
            int x = comparisonMap.get(i).applyAsInt(o1, o2);
            if(x != 0) 
                return x;
        }
        return 0;
    }
    
}
