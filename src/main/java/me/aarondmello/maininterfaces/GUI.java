package me.aarondmello.maininterfaces;
import java.util.HashMap;

import me.aarondmello.datatypes.Tournament;

public interface GUI{
    public void displayWelcomeScreen();
    public boolean getIfNewTournament();
    public Tournament getNewTournament();
    public Tournament getExistingTournament();
    public HashMap<String,int[]> getRoundResults(Tournament tournament);
    public void displayResults(Tournament tournament);
    public boolean getIfSavingResults();
    public boolean wasCancelPressed();
}