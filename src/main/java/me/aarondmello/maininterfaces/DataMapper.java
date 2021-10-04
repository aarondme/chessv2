package me.aarondmello.maininterfaces;

import me.aarondmello.datatypes.Tournament;

public interface DataMapper{
    public void writeToFile();
    /**
     * @return true iff successful
     */
    public boolean readFromStarterFile(Tournament tournament);
    /**
     * @return true iff successful
     */
    public boolean readFromInProgressFile(Tournament tournament, int roundNumber);
}