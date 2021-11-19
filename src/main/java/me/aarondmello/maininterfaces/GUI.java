package me.aarondmello.maininterfaces;

import java.io.File;
import java.util.Iterator;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.FileReadSummary;
import me.aarondmello.driver.TournamentManager;
public interface GUI{
    public void start(TournamentManager tournamentManager);

    public File getSaveLocation();

    public void confirmTournamentDetails(Tournament tournament, Iterator<FileReadSummary> iterator);

    public void getTournamentDetails(Tournament tournament);
}