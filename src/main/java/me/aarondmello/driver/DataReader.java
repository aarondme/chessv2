package me.aarondmello.driver;

import me.aarondmello.datatypes.Tournament;
import java.io.BufferedReader;
import java.io.IOException;

public interface DataReader{
    void readFromStarterFile(BufferedReader reader, Tournament tournament) throws IOException;
    Tournament readFromInProgressFile(BufferedReader reader);
    void readRoundResults(Tournament tournament, String fileName);

    void readRoundResults(Tournament t);
}