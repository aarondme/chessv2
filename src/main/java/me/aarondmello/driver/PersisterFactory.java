package me.aarondmello.driver;

import java.io.File;
import java.util.Iterator;

import me.aarondmello.datatypes.Tournament;

public interface PersisterFactory {
    public Persister getPersister();
}
