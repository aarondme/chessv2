package me.aarondmello.commandlineinterface;

import java.io.File;
import java.util.Scanner;

import me.aarondmello.datatypes.Division;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.FileReadSummary;
import me.aarondmello.driver.Persister;
import me.aarondmello.driver.PersisterFactory;
import me.aarondmello.maininterfaces.GUI;
public class CommandLineInterface implements GUI {

    private PersisterFactory persisterFactory;
    private Scanner input = new Scanner(System.in);

    @Override
    public void start(PersisterFactory persisterFactory){
        this.persisterFactory = persisterFactory;
        Persister persister = persisterFactory.getPersister();
        Tournament tournament;
        displayWelcomeMessage();
        tournament = getTournament(persister);
        runTournament(tournament);
        saveTournament(tournament);
    }

    private void saveTournament(Tournament tournament) {
    }

    private void runTournament(Tournament tournament) {
    }

    private Tournament getTournament(Persister persister) {
        int in = promptForInt("Enter the appropriate number to continue", 
                        new String[]{"0: exit", "1: starting new tournament", "2: resuming existing tournament"}, 
                        0, 2);
        if(in == 0)
            return null;
        else if(in == 1)
            return getNewTournamentDetails(persister);
        else
            return getExistingTournamentDetails(persister);
    }

    private Tournament getNewTournamentDetails(Persister persister) {
        int in = promptForInt("Enter the appropriate number to continue", 
                        new String[]{"0: exit", "1: starting from save", "2: starting from scratch"}, 
                        0, 2);
        Tournament tournament = new Tournament();
        if(in == 0)
            return null;
        if(in == 1){
            File tournamentFolder = getSaveLocation("Enter the path to the folder in which tournament data is stored, or enter \"0\" to exit");
            tournament = persister.scanFolder(tournamentFolder);
        }
        return editNewTournamentDetails(tournament, persister);
    }

    private Tournament getExistingTournamentDetails(Persister persister) {
        return null;
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

    private File getSaveLocation(String prompt) {
        while(true){
            System.out.println(prompt);
            String val = input.nextLine();
            if(val.equals("0"))
                return null;
            
            File folder = new File(val);
            if(folder.exists() && folder.isDirectory())
                return folder;

            System.out.println("Invalid input provided");
        }
        
    }

    private Tournament confirmTournamentDetails(Tournament tournament, Persister persister) {
        while(true){
            printPlayerList(tournament);
            int in = promptForInt("--- Confirming tournament details ---", new String[]{"0: close program", "1: continue", "2: edit tournament details"}, 
                        0, 2);
            
            if(in == 0)
                return null;
            else if(in == 1)
                return tournament;
            else
                return editNewTournamentDetails(tournament, persister);
        }
    }

    private void printPlayerList(Tournament tournament) {
        System.out.println("--- Tournament details ---");
        System.out.println("Tournament name " + tournament.getName());
        System.out.println("Number of rounds " + tournament.getTotalRounds());
        for(Division division : tournament.getDivisions()){
            System.out.println("  Division name " + division.getName());
            System.out.println("    Player ID | Player name | Organization");
            for(Player player : division.getPlayers()){
                System.out.println("    " + player.getID() + " | " + player.getName() + " | " + player.getOrganization());
            }
        }
    }

    private Tournament editNewTournamentDetails(Tournament tournament, Persister persister) {
        while(true){
            printPlayerList(tournament);
            int in = promptForInt("--- Fetching tournament details ---\nEnter the appropriate number to continue", 
                        new String[]{"0: close program", "1: edit tournament name", "2: edit number of rounds", "3: add file", "4: add player",
                        "5: edit player", "6: remove player", "7: done"}, 0, 7);

            if(in == 0)
                return null;
            else if(in == 1)
                tournament.setName(getNewTournamentName());
            else if(in == 2)
                tournament.setTotalRounds(getNewTournamentTotalRounds());
            else if(in == 3){
                //TODO
            }
            else if(in == 4)
                addPlayerToTournament(tournament);
            else if (in == 5)
                editPlayerInTournament(tournament);
            else if(in == 6)
                removePlayerInTournament(tournament);
            else if(in == 7){
                if(tournament.isDataValid())
                    return confirmTournamentDetails(tournament, persister);
                return null;
            }
        }  
    }

    private void removePlayerInTournament(Tournament tournament) {
        System.out.println("Enter the division of the player");
        String division = input.nextLine().trim();
        int id = promptForInt("Enter the player id", new String[]{}, 0, 2000);
        tournament.removePlayer(division, id);
    }

    private void editPlayerInTournament(Tournament tournament) {
        System.out.println("Enter the division of the player");
        String division = input.nextLine().trim();
        int id = promptForInt("Enter the player id", new String[]{}, 0, 2000);
        Player player = tournament.getPlayer(division, id);
        System.out.println("Enter the player name");
        String playerName = input.nextLine().trim();
        System.out.println("Enter the player organization");
        String playerOrganization = input.nextLine().trim();
        player.setName(playerName);
        player.setOrganization(playerOrganization);
    }

    private void addPlayerToTournament(Tournament tournament) {
        System.out.println("Enter the division of the player");
        String division = input.nextLine().trim();
        System.out.println("Enter the player name");
        String playerName = input.nextLine().trim();
        System.out.println("Enter the player organization");
        String playerOrganization = input.nextLine().trim();

        tournament.addPlayer(division, new Player(playerName, playerOrganization));
    }

    private int getNewTournamentTotalRounds() {
        return promptForInt("Enter the number of rounds", new String[]{}, 1, 30);
    }

    private String getNewTournamentName() {
        System.out.println("Enter the name of the tournament");
        return input.nextLine().trim();
    }
    
}
