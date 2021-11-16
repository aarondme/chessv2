package me.aarondmello.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.maininterfaces.DataReader;
import me.aarondmello.maininterfaces.DataWriter;

public class Persister {
    ArrayList<FileReadSummary> filesRead = new ArrayList<FileReadSummary>();

    public Tournament scanFolder(File tournamentFolder) {
        File configFile = new File(tournamentFolder, "/config.txt");
        if(configFile.exists()){
            try {
                FileReadSummary configSummary = new FileReadSummary("config.txt");
                readConfig(new BufferedReader(new FileReader(configFile)), configSummary);
                filesRead.add(configSummary); 
            } catch (Exception e) {
                System.out.println("Error when reading file");
            }      
        }
        return null;
    }

    public void readConfig(BufferedReader reader, FileReadSummary summary) {
        summary.setErrorOccured(true);
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
