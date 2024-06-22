package me.aarondmello.commandlineinterface;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.DataReader;

import java.io.File;

public interface BasicPrompts {
    void displayWelcomeMessage();
    boolean getIfStartingNewTournament();
    File getLocationOfExistingTournament();
    Tournament getNewTournamentDetails(DataReader dataReader);
    void alterPlayersSittingOut(Tournament t);
    void getRoundResults(Tournament t, DataReader reader);
    void displayStandings(Tournament t);
    void displayFileSaveError();
}
