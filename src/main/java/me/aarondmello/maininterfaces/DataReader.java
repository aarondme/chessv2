package me.aarondmello.maininterfaces;

import me.aarondmello.datatypes.Tournament;

public interface DataReader{
    /**
     * @return true iff successful
     */
    boolean readFromStarterFile(Tournament tournament);
    /**
     * @return true iff successful
     */
    boolean readFromInProgressFile(Tournament tournament, int roundNumber);
}