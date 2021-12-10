package me.aarondmello.maininterfaces;

import java.io.File;
import java.util.Iterator;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.FileReadSummary;
import me.aarondmello.driver.TournamentManager;
public interface GUI{
    public void start(TournamentManager tournamentManager);

    /**
     * @return The folder in which the config and data files should be loaded from, and where the output is saved
     */
    public File getSaveLocation();

    public Tournament confirmTournamentDetails(Tournament tournament, Iterator<FileReadSummary> iterator);

    public Tournament getTournamentDetails(Tournament tournament);
}