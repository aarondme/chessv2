package me.aarondmello.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import me.aarondmello.csv.CsvReader;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Tournament;

public class Persister {
    ArrayList<FileReadSummary> filesRead = new ArrayList<FileReadSummary>();

    public Tournament scanFolder(File tournamentFolder) {
        File configFile = new File(tournamentFolder, "/config.txt");
        Tournament tournament = null;
        if(configFile.exists()){
            try {
                FileReadSummary configSummary = new FileReadSummary("config.txt");
                tournament = readConfig(new BufferedReader(new FileReader(configFile)), configSummary);
                filesRead.add(configSummary); 
            } catch (Exception e) {
                System.out.println("Error when reading file");
            }      
        }
        return tournament;
    }

    public Tournament readConfig(BufferedReader reader, FileReadSummary summary) {
        Tournament tournament = new Tournament();
        String line;
        try {
            while((line = reader.readLine()) != null){
                String[] lineArgs = line.split("=");
                if(lineArgs[0].trim().equals("tournament_name")){
                    tournament.setName(lineArgs[1].trim());
                }
                else if(lineArgs[0].trim().equals("number_of_rounds")){
                    tournament.setTotalRounds(Integer.parseInt(lineArgs[1].trim()));
                }
                else{
                    throw new Exception();
                }
            } 
        } catch (Exception e) {
            summary.setErrorOccured(true);
            return tournament;
        }
        return tournament;
    }

    public void addPlayersInFileToTournament(BufferedReader reader, Tournament tournament){
        CsvReader csvReader = new CsvReader();
        HashMap<String, ArrayList<Player>> players = csvReader.read(reader);
        tournament.addPlayers(players);
        //TODO
    }

    public Iterator<FileReadSummary> getFilesReadIterator() {
        return filesRead.iterator();
    }
    public int getNumberOfFilesRead(){
        return filesRead.size();
    }

    public boolean wasFileRead(String relativePath){
        for(FileReadSummary fileReadSummary : filesRead){
            if(fileReadSummary.getRelativePathToFile().equals(relativePath))
                return true;
        }
        return false;
    }

    public boolean wasFileReadSuccessfully(String relativePath) {
        for(FileReadSummary fileReadSummary : filesRead){
            if(fileReadSummary.getRelativePathToFile().equals(relativePath))
                return !fileReadSummary.didErrorOccur();
        }
        return false;
    }
}
