package me.aarondmello.driver;

import me.aarondmello.datatypes.Tournament;

public interface SimpleUI {
    void displayWelcomeMessage();

    Tournament getTournament(DataReader tournamentReader);

    void getRoundResults(Tournament tournament);

    void alterSitOuts(Tournament tournament);

    void displayStandings(Tournament tournament);
}
