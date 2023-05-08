package me.aarondmello.driver;

import me.aarondmello.datatypes.Tournament;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
//TODO add "enable saves" option..; fix persisterFactory.
public class SimpleProgramFlow implements GUI{
    
    SimpleUI ui;
    
    SimpleProgramFlow(SimpleUI ui){
        this.ui = ui;
    }
    @Override
    public void start(PersisterFactory persisterFactory){
        DataReader tournamentReader = persisterFactory.getReaderOfType("csv");
        DataWriter tournamentWriter = persisterFactory.getWriterOfType("csv");
        Tournament tournament;
        ui.displayWelcomeMessage();
        tournament = ui.getTournament(tournamentReader);
        runTournament(tournament, tournamentWriter);
    }

    private void runTournament(Tournament tournament, DataWriter tournamentWriter) {
        if(tournament == null) return;

        boolean isValid = false;
        while(tournament.hasRoundsRemaining()){
            saveTournament(tournament, tournamentWriter);
            tournament.createRound();

            while(!isValid){
                ui.getRoundResults(tournament);
                isValid = tournament.confirmRoundResults();
            }

            ui.alterSitOuts(tournament);

            ui.displayStandings(tournament);
            isValid = false;
        }
        saveTournament(tournament, tournamentWriter);
    }

    private void saveTournament(Tournament tournament, DataWriter writer) {
        if(tournament == null) return;

        try {
            File f = new File(tournament.getName() + "_" + tournament.getRoundNumber() + ".csv");
            PrintWriter p = new PrintWriter(f);
            writer.saveTournament(tournament, p);
            p.flush();
            p.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to save tournament to file");
        }

    }

}
