package me.aarondmello.commandlineinterface;

import me.aarondmello.datatypes.*;
import me.aarondmello.driver.DataReader;
import me.aarondmello.driver.DataWriter;
import me.aarondmello.driver.GUI;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class CsvInterface implements GUI {

    private final Scanner input;
    public CsvInterface(Scanner input){
        this.input = input;
    }
    @Override
    public void start(DataReader tournamentReader, DataWriter tournamentWriter){
        Tournament tournament;
        displayWelcomeMessage();
        tournament = getTournament(tournamentReader);
        runTournament(tournament, tournamentWriter);
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
                System.err.println("Unable to save tournament to file");
            }

    }

    private void runTournament(Tournament tournament, DataWriter writer) {
        if(tournament == null) return;

        saveTournament(tournament, writer);
        boolean isValid = false;
        while(tournament.hasRoundsRemaining()){
            tournament.createRound();
            String fileName = saveRound(tournament);
            while(!isValid){
                getRoundResults(tournament, fileName); //Extract from save
                isValid = tournament.confirmRoundResults();
                if(!isValid)
                    System.out.println("Error in file.");
            }

            isValid = false;
            saveTournament(tournament, writer);
        }
    }

    private String saveRound(Tournament tournament){
        String fileName = String.format("%s_Round %d_Pairing.csv", tournament.getName(), tournament.getRoundNumber());
        PrintWriter writer;
        boolean isFinished = false;
        try {
            writer = new PrintWriter(fileName);
            writer.println(tournament.getName());
            writer.println(String.format("Round,%d", tournament.getRoundNumber()));
            for (Division d: tournament.getDivisions()) {
                writer.println(String.format("Division,%s", d.getName()));
                writer.println("Game ID,White,Black,Result");
                int i = 1;
                for (Game g :d.getPairing()) {
                    writer.println(String.format("%d,%s,%s,%s",
                            i, formatPlayer(g.getWhitePlayer()), formatPlayer(g.getBlackPlayer()),
                            (g.getResult() == null)? "":g.getResult().toString().charAt(0)));
                    i++;
                }
            }
            writer.flush();
            writer.close();
            isFinished = true;
        }catch (IOException e){
            System.err.println("Error when saving file");
        }
        if(isFinished)
            System.out.printf("Pairing for round %d saved as csv\n", tournament.getRoundNumber());
        return fileName;
    }

    private String formatPlayer(Player player) {
        return player.getName() + " (" + player.getOrganization() + ") [" + player.getScore() + "]";
    }

    private void getRoundResults(Tournament tournament, String fileName) {
        System.out.println("Press enter when the csv is filled");
        input.nextLine();
        try {
            String nextLine;
            String divisionName = "";
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
            fileReader.readLine();
            fileReader.readLine();
            while ((nextLine = fileReader.readLine()) != null){
                String[] splitLine = nextLine.split(",");
                if(splitLine[0].equals("Division")){
                    divisionName = splitLine[1];
                }
                else if (!splitLine[0].equals("Game ID") && splitLine.length > 3) {
                    tournament.setResultByDivisionAndGameID(divisionName, Integer.parseInt(splitLine[0]) - 1,
                            toGameResult(splitLine[3]));
                }
            }
        }catch (IOException ignored){}

    }

    private GameResult toGameResult(String s){
        if(s.startsWith("W") || s.startsWith("w"))
            return GameResult.WHITE_WIN;
        if(s.startsWith("B") || s.startsWith("b"))
            return GameResult.BLACK_WIN;
        if (s.startsWith("D") || s.startsWith("d"))
            return GameResult.DRAW;
        return null;
    }

    private Tournament getTournament(DataReader tournamentReader) {
        int in = promptForInt("Enter the appropriate number to continue", 
                        new String[]{"0: exit", "1: starting new tournament", "2: resuming existing tournament"}, 
                        0, 2);
        if(in == 0)
            return null;
        else if(in == 1)
            return editNewTournamentDetails(new Tournament(), tournamentReader);
        else
            return getExistingTournamentDetails(tournamentReader);
    }


    private Tournament getExistingTournamentDetails(DataReader tournamentReader) {
        File toReadFrom = getLocationToReadSave();
        try {
            Tournament t = tournamentReader.readFromInProgressFile(new BufferedReader(new FileReader(toReadFrom)));
            t.initialize(false);
            return t;
        }catch (Exception e){
            return null;
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

    private void displayWelcomeMessage() {
        System.out.println("--- Chess Tournament Manager by Aaron D'Mello---");
        System.out.println("Licensed under CC BY-SA 4.0");
        System.out.println("http://creativecommons.org/licenses/by-sa/4.0/?ref=chooser-v1");
    }

    private File getLocationToReadSave(){
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

    private Tournament editNewTournamentDetails(Tournament tournament, DataReader tournamentReader) {
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
                tournament.toggleType();
            else if(in == 8){
                if(tournament.isDataValid())
                    return confirmTournamentDetails(tournament, tournamentReader);
                System.out.println("Tournament data is invalid");
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
