package me.aarondmello.driver;

public final class TiebreakIndex {
    public final static int BUCHHOLZ_CUT_ONE = 0;
    public final static int BUCHHOLZ = 1;
    public final static int SONNEBORN_BERGER = 2;
    public final static int PROGRESSIVE_SCORES = 3;
    public final static int DIRECT_ENCOUNTER = -1; //Negative values for tiebreaks not implemented by TiebreakCalculator class
    public final static int WIN_COUNT = 4;
    public final static int WIN_COUNT_AS_BLACK = 5;
}
