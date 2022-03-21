package me.aarondmello.maininterfaces;

import me.aarondmello.datatypes.Tournament;

import java.io.File;

public interface DataWriter{
    void writeToFile(File file, Tournament tournament);
}