package me.aarondmello.driver;

import me.aarondmello.datatypes.Tournament;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;

public interface DataWriter{
    void saveTournament(Tournament tournament, PrintWriter writer);
}