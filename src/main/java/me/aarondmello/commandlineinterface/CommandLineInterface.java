package me.aarondmello.commandlineinterface;

import java.io.File;
import java.util.Iterator;
import java.util.Scanner;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.FileReadSummary;
import me.aarondmello.driver.TournamentManager;
import me.aarondmello.maininterfaces.GUI;
public class CommandLineInterface implements GUI {

    private TournamentManager tournamentManager;
    private Tournament tournament;
    private boolean shouldExit;
    private Scanner input = new Scanner(System.in);

    @Override
    public void start(TournamentManager tournamentManager){
        this.tournamentManager = tournamentManager;
        displayWelcomeMessage();
        getTournament();
        runTournament();
        saveTournament();
    }

    private void saveTournament() {
    }

    private void runTournament() {
    }

    private void getTournament() {
        int in = promptForInt("Enter the appropriate number to continue", 
                        new String[]{"0: exit", "1: starting new tournament", "2: resuming existing tournament"}, 
                        0, 2);
        if(in == 0)
            return;
        else if(in == 1)
            tournament = tournamentManager.createTournament();
        else
            tournament = tournamentManager.resumeTournament();
    }

    private int promptForInt(String header, String[] options, int min, int max) {
        System.out.println(header);
        while(true){
            for(String option : options)
                System.out.println(option);
            String val = input.nextLine();
            try {
                int out = Integer.parseInt(val);
                if(min <= out && out <= max)
                    return out;
            } catch (Exception e) {
      
            }
            System.out.println("Invalid input provided");
        }
    }

    private void displayWelcomeMessage() {
        System.out.println("--- Chess Tournament Manager ---");
    }

    @Override
    public File getSaveLocation() {
        while(true){
            System.out.println("Enter the path to the folder in which tournament data is stored, or enter \"0\" to exit");
            String val = input.nextLine();
            if(val.equals("0"))
                return null;
            
            File folder = new File(val);
            if(folder.exists() && folder.isDirectory())
                return folder;

            System.out.println("Invalid input provided");
        }
        
    }

    @Override
    public Tournament confirmTournamentDetails(Tournament tournament, Iterator<FileReadSummary> iterator) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tournament getTournamentDetails(Tournament tournament) {
        while(true){
            int in = promptForInt("--- Fetching tournament details ---\nEnter the appropriate number to continue", 
                        new String[]{"0: close program", "1: edit tournament name", "2: edit number of rounds", "3: add file", "4: done"}, 
                         0, 4);

            if(in == 0)
                return null;
            else if(in == 1)
                tournament.setName(getNewTournamentName());
            else if(in == 2)
                tournament.setTotalRounds(getNewTournamentTotalRounds());
            else if(in == 3){}
            else if(tournament.isDataValid())
                return tournament;
            return null;
        }  
    }

    private int getNewTournamentTotalRounds() {
        return 0;
    }

    private String getNewTournamentName() {
        return null;
    }
    
}
