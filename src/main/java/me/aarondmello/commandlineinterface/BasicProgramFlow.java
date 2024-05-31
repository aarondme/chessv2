package me.aarondmello.commandlineinterface;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.DataReader;
import me.aarondmello.driver.DataWriter;
import me.aarondmello.driver.GUI;

import java.io.*;

public class BasicProgramFlow implements GUI {
    final BasicPrompts input;

    public BasicProgramFlow(BasicPrompts input){
        this.input = input;
    }
    @Override
    public void start(DataReader tournamentReader, DataWriter tournamentWriter) {
        Tournament tournament;
        input.displayWelcomeMessage();
        tournament = getTournament(tournamentReader);
        runTournament(tournament, tournamentReader, tournamentWriter);
    }

    private void saveTournament(Tournament tournament, DataWriter writer) {
        if(tournament == null) return;

        try {
            File f = new File(String.format("%s_Round %d.csv", tournament.getName(), tournament.getRoundNumber()));
            PrintWriter p = new PrintWriter(f);
            writer.saveTournament(tournament, p);
            p.flush();
            p.close();
        } catch (FileNotFoundException e) {
            input.displayFileSaveError();
        }

    }

    private void runTournament(Tournament tournament, DataReader reader, DataWriter writer) {
        if(tournament == null) return;

        saveTournament(tournament, writer);
        while(tournament.hasRoundsRemaining()){
            tournament.createRound();
            String fileName = String.format("%s_Round %d_Pairing.csv", tournament.getName(), tournament.getRoundNumber());
            try {
                PrintWriter printWriter = new PrintWriter(fileName);
                writer.saveRound(tournament, printWriter);
                System.out.printf("Pairing for round %d saved as csv in file %s\n", tournament.getRoundNumber(), fileName);
            }catch (IOException e){
                System.err.println("Error when saving file");
            }

            input.getRoundResults(tournament, reader);
            input.alterPlayersSittingOut(tournament);
            saveTournament(tournament, writer);
            input.displayStandings(tournament);
        }
    }

    private Tournament getTournament(DataReader tournamentReader) {
        boolean isStartingNewTournament = input.getIfStartingNewTournament();

        if(isStartingNewTournament)
            return input.editNewTournamentDetails(new Tournament(), tournamentReader);
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
