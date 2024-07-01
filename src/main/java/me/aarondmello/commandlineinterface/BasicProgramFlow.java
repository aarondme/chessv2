package me.aarondmello.commandlineinterface;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.DataReader;
import me.aarondmello.driver.DataWriter;

import java.io.*;

public class BasicProgramFlow {
    final BasicPrompts input;

    public BasicProgramFlow(BasicPrompts input){
        this.input = input;
    }

    public void start(DataReader tournamentReader, DataWriter tournamentWriter) {
        Tournament tournament;
        input.displayWelcomeMessage();
        tournament = getTournament(tournamentReader);
        runTournament(tournament, tournamentReader, tournamentWriter);
    }

    private void saveTournament(Tournament tournament, DataWriter writer) {
        try {
            writer.saveTournament(tournament);
        } catch (IOException e) {
            input.displayFileSaveError();
        }
    }

    private void runTournament(Tournament tournament, DataReader reader, DataWriter writer) {
        if(tournament == null) return;

        saveTournament(tournament, writer);
        while(tournament.hasRoundsRemaining()){
            input.alterPlayersSittingOut(tournament);
            tournament.createRound();

            try {
                writer.saveRound(tournament);
            }catch (IOException e){
                input.displayFileSaveError();
            }

            input.getRoundResults(tournament, reader);
            saveTournament(tournament, writer);
            input.displayStandings(tournament);
        }
    }

    private Tournament getTournament(DataReader tournamentReader) {
        boolean isStartingNewTournament = input.getIfStartingNewTournament();

        if(isStartingNewTournament)
            return input.getNewTournamentDetails(tournamentReader);
        else
            return getExistingTournamentDetails(tournamentReader);
    }

    private Tournament getExistingTournamentDetails(DataReader tournamentReader) {
        File toReadFrom = input.getLocationOfExistingTournament();
        try {
            Tournament t = tournamentReader.readFromInProgressFile(new BufferedReader(new FileReader(toReadFrom)));
            t.initialize(false);
            return t;
        }catch (Exception e){
            return null;
        }
    }

}
