package me.aarondmello.maininterfaces;

import me.aarondmello.datatypes.Tournament;

public interface DataExporter{
    public void writeToFile();
    public Tournament readFromStarterFile();
    public Tournament readFromInProgressFine(int number);
}