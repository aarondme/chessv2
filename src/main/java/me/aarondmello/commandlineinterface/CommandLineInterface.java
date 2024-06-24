package me.aarondmello.commandlineinterface;

import me.aarondmello.datatypes.*;
import me.aarondmello.driver.DataReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface implements BasicPrompts {

    private final Scanner input;
    public CommandLineInterface(Scanner input){
        this.input = input;
    }

    @Override
    public void alterPlayersSittingOut(Tournament tournament) {
        int a = promptForInt("Enter the appropriate number to continue",
                new String[]{"0: continue", "1: modify players sitting out"}, 0, 1);
        if(a == 0) return;

        while(true){
            printPlayerList(tournament);

            System.out.println("Enter \"division\" \"player id\" \"shouldSitOut\", where shouldSitOut is 1 if sitting out, 0 otherwise. Enter \"0\" to exit");
            String in = input.nextLine();
            if(in.equals("0"))
                return;
            try{
                String[] abc = in.split("\\s+");
                String divName = abc[0];
                int id = Integer.parseInt(abc[1]);
                int shouldSitOut = Integer.parseInt(abc[2]);
                tournament.getPlayer(divName, id).setActive(shouldSitOut == 0);
            }catch (Exception e){
                System.out.println("Invalid input provided.");
            }
        }
    }

    @Override
    public void displayStandings(Tournament tournament) {
        System.out.println("--- Tournament standings ---");
        System.out.println("Tournament name " + tournament.getName());
        if(tournament.getRoundNumber() <= tournament.getTotalRounds())
            System.out.println("Round " + tournament.getRoundNumber() + " of " + tournament.getTotalRounds());
        else
            System.out.println(tournament.getTotalRounds() + "-Round Tournament Complete");
        for(Division division : tournament.getDivisions()){
            System.out.println("Division name " + division.getName());
            System.out.print("| ID | Rank |         Player name         |       Organization       | Score |");
            System.out.print("/////");
            System.out.println("| ID | Rank |         Player name         |       Organization       | Score |");
            ArrayList<String> strings = new ArrayList<>();


            for(Player player : division.getPlayers()){
                strings.add(String.format("|%1$-4s|%2$-6s|%3$-29s|%4$-26s|%5$-7s|", player.getID(), player.getRank(), player.getName(), player.getOrganization(), player.getScore()));
            }
            for (int i = 0; i < strings.size() / 2; i++) {
                System.out.println(strings.get(i) + "/////" + strings.get(strings.size()/2 + i));
            }
            if(strings.size() % 2 == 1)
                System.out.println(strings.get(strings.size() - 1));
        }
    }

    @Override
    public void displayFileSaveError() {
        System.err.println("Error saving tournament");
    }

    private GameResult toGameResult(int num){
        if(num == 2) return GameResult.WHITE_WIN;
        if(num == 1) return GameResult.DRAW;
        if(num == 0) return GameResult.BLACK_WIN;
        return null;
    }

    @Override
    public void getRoundResults(Tournament tournament, DataReader reader) {
        while(true){
            getResultFromInput(tournament);
            if(tournament.confirmRoundResults())
                return;
            System.out.println("Round results not filled");
        }  
    }

    private void printPairing(Tournament tournament) {
        System.out.println("--- Pairing details ---");
        System.out.println("Tournament name " + tournament.getName());
        System.out.println("Round " + tournament.getRoundNumber() + " of " + tournament.getTotalRounds());
        for(Division division : tournament.getDivisions()){
            System.out.println("Division " + division.getName());
            System.out.printf("|%1$-4s|%2$-50s|%3$-50s|%4$-10s|\n", "ID", "White", "Black", "result");
            int id = 1;
            for(Game game : division.getPairing()){
                System.out.printf("|%1$-4s|%2$-50s|%3$-50s|%4$-10s|\n", id, formatPlayer(game.getWhitePlayer()), formatPlayer(game.getBlackPlayer()), game.getResult());
                id++;
            }
        }
        System.out.println();
    }

    private String formatPlayer(Player player) {
        return player.getName() + " (" + player.getOrganization() + ") [" + player.getScore() + "]";
    }

    private void getResultFromInput(Tournament tournament) {
        while(true){
            printPairing(tournament);
            System.out.println("To enter results individually, enter \"division\" \"game number\" \"game result\", where round result is 2 if white win, 1 if draw, 0 if black win.");
            System.out.println("To enter results sequentially, enter \"0\"");
            System.out.println("Enter \"DONE\" to exit");
            String in = input.nextLine();
            if(in.equals("0"))
                getResultsSequentially(tournament);
            if(in.equals("DONE"))
                return;
            try {
                String[] split = in.split("\\s+");
                GameResult result = toGameResult(Integer.parseInt(split[2]));
                tournament.setResultByDivisionAndGameID(split[0], Integer.parseInt(split[1]) - 1, result);
            } catch (Exception e) {
                System.out.println("Invalid input provided.");
            }
        }
    }

    private void getResultsSequentially(Tournament tournament) {
        for (Division d: tournament.getDivisions()) {
            int id = 1;
            for (Game g: d.getPairing()) {
                int resultAsInt = promptForInt("Enter the result of the following game. Enter 2 for white win," +
                        "1 for a draw, 0 for black win\n" + "Division " + d.getName() + " Game id " + id +
                        "White: " + formatPlayer(g.getWhitePlayer()) + " Black: " +
                        formatPlayer(g.getBlackPlayer()), new String[0], 0, 2);
                GameResult result = toGameResult(resultAsInt);
                g.setResult(result);
                id++;
            }

        }
    }


    private int promptForInt(String header, String[] options, int min, int max) {
        System.out.println(header);
        while(true){
            for(String option : options)
                System.out.println(option);
            String val = input.nextLine().strip();
            try {
                int out = Integer.parseInt(val);
                if(min <= out && out <= max)
                    return out;
            } catch (Exception e) {
                System.out.println("Invalid input provided");
            }
        }
    }

    @Override
    public void displayWelcomeMessage() {
        System.out.println("--- Chess Tournament Manager by Aaron D'Mello---");
        System.out.println("Licensed under CC BY-SA 4.0");
        System.out.println("http://creativecommons.org/licenses/by-sa/4.0/?ref=chooser-v1");
    }

    @Override
    public boolean getIfStartingNewTournament() {
        int input = promptForInt("Enter the appropriate number to continue",
                new String[]{"0: exit", "1: starting new tournament", "2: resuming existing tournament"},
                0, 2);
        return input == 1; //TODO: exit on 0.
    }

    @Override
    public File getLocationOfExistingTournament(){
        File result;
        while (true) {
            System.out.println("Enter the path where the tournament was saved, or \"0\" to exit");
            String val = input.nextLine();
            if (val.equals("0")) {
                result = null;
                break;
            }

            File f = new File(val);
            if (f.exists() && !f.isDirectory()) {
                result = f;
                break;
            }

            System.out.println("Invalid input provided");
        }

        return result;
    }

    private Tournament confirmTournamentDetails(Tournament tournament, DataReader tournamentReader) {
        printPlayerList(tournament);
        int in = promptForInt("--- Confirming tournament details ---",
                new String[]{"0: close program", "1: continue", "2: edit tournament details"},
                0, 2);
            
        if(in == 0)
            return null;
        else if(in == 1) {
            tournament.initialize(true);
            return tournament;
        }
        else
            return editNewTournamentDetails(tournament, tournamentReader);

    }

    private void printPlayerList(Tournament tournament) {
        System.out.println("--- Tournament details ---");
        System.out.println("Tournament name " + tournament.getName());
        System.out.println("Number of rounds " + tournament.getTotalRounds());
        System.out.println("Divisional Tournament? " + tournament.isRegionalTournament());
        for(Division division : tournament.getDivisions()){
            System.out.println("Division name " + division.getName());
            System.out.print("| ID |         Player name         |         Organization        | Active |");
            System.out.print("/////");
            System.out.println("| ID |         Player name         |         Organization        | Active |");
            boolean shouldStartNewLine = false;
            for(Player p : division.getPlayers()){
                System.out.printf("|%1$-4s|%2$-29s|%3$-29s|%4$-8s|", p.getID(), p.getName(), p.getOrganization(), p.isActive());
                System.out.print((shouldStartNewLine)?"\n":"/////");
                shouldStartNewLine = !shouldStartNewLine;
            }
            System.out.println();
        }
    }

    @Override
    public Tournament getNewTournamentDetails(DataReader tournamentReader){
        String tournamentName = getNewTournamentName();
        int numRounds = getNewTournamentTotalRounds();
        boolean isDivisional = promptForInt("Enter the appropriate number to continue",
                new String[]{"0: divisional tournament", "1: finals tournament"}, 0, 1) == 0;
        return editNewTournamentDetails(new Tournament(tournamentName, numRounds, isDivisional), tournamentReader);
    }

    public Tournament editNewTournamentDetails(Tournament tournament, DataReader tournamentReader) {
        while(true){
            printPlayerList(tournament);
            int in = promptForInt("--- Fetching tournament details ---\nEnter the appropriate number to continue", 
                        new String[]{"0: close program", "1: edit tournament name", "2: edit number of rounds", "3: add file", "4: add player",
                        "5: edit player", "6: remove player", "7: Toggle Divisional/Final","8: done"}, 0, 8);

            if(in == 0)
                return null;
            else if(in == 1)
                tournament.setName(getNewTournamentName());
            else if(in == 2)
                tournament.setTotalRounds(getNewTournamentTotalRounds());
            else if(in == 3){
                try {
                    tournamentReader.readFromStarterFile(new BufferedReader(new FileReader(getFile())), tournament);
                }catch (Exception e) {
                    System.out.println("Error reading file");
                }
            }
            else if(in == 4)
                addPlayerToTournament(tournament);
            else if (in == 5)
                editPlayerInTournament(tournament);
            else if(in == 6)
                removePlayerInTournament(tournament);
            else if(in == 7)
                tournament.setRegionalTournament(!tournament.isRegionalTournament());
            else if(in == 8){
                return confirmTournamentDetails(tournament, tournamentReader);
            }
        }  
    }

    private File getFile() {
        System.out.println("Enter the file to read");
        return new File(input.nextLine().trim());
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
        int id = promptForInt("Enter the player id to edit", new String[]{}, 0, 2000);
        Player player = tournament.getPlayer(division, id);
        System.out.println("Enter the corrected player name");
        String playerName = input.nextLine().trim();
        System.out.println("Enter the corrected player organization");
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
