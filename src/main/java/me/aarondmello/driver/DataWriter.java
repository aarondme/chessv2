package me.aarondmello.driver;

import me.aarondmello.datatypes.Tournament;

import java.io.PrintWriter;

public interface DataWriter{
    void saveTournament(Tournament tournament, PrintWriter writer);
}