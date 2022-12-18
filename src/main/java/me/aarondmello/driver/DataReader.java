package me.aarondmello.driver;

import me.aarondmello.datatypes.Tournament;
import java.io.BufferedReader;

public interface DataReader{
    void readFromStarterFile(BufferedReader reader, Tournament tournament);
    Tournament readFromInProgressFile(BufferedReader reader);
}