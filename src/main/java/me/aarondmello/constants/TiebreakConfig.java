package me.aarondmello.constants;

public final class TiebreakConfig {
    public final static int[] NO_RATING_SOLO = 
        {TiebreakIndex.BUCHHOLZ_CUT_ONE, TiebreakIndex.BUCHHOLZ,
        TiebreakIndex.SONNEBORN_BERGER, TiebreakIndex.PROGRESSIVE_SCORES, 
        TiebreakIndex.DIRECT_ENCOUNTER, TiebreakIndex.WIN_COUNT, TiebreakIndex.WIN_COUNT_AS_BLACK};
}
