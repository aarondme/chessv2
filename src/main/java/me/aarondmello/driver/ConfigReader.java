package me.aarondmello.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.maininterfaces.GUI;
import me.aarondmello.maininterfaces.DataMapper;

public class ConfigReader {
    final static int CSV = 1;

    public static void init(File tournamentFolder, Tournament tournament, GUI gui, LinkedList<DataMapper> mappers) throws IOException{
        int[] dataTransferType = {-1, -1};
        readConfig(tournamentFolder, tournament, gui, dataTransferType);
    }
    private static void readConfig(File tournamentFolder, Tournament tournament, GUI gui, int[] dataTransferType) throws IOException{
        if(tournamentFolder.isFile()){
            tournament = null;
            return;
        }
            
        File configFile = new File(tournamentFolder.toPath() + "/config.txt");
        if(!configFile.exists()){
            tournament = null;
            return;
        }
    }
}
