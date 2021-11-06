package me.aarondmello.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.maininterfaces.DataReader;
import me.aarondmello.maininterfaces.DataWriter;

public class ConfigReader {
    final static int CSV = 1;
   
    private void readConfig(File tournamentFolder) throws IOException{            
        File configFile = getFileIfExists(tournamentFolder);

        BufferedReader input = new BufferedReader(new FileReader(configFile));
        String next = input.readLine();
        Tournament tournament = new Tournament();
        int dataInType = -1;
        int dataOutType = -1;
        boolean isConfigValid = false;
        while(next != null){
            next = next.trim();
            if(next.length() > 0 && next.charAt(0) != '#'){
                String[] line = next.split(":");
                if(line.length != 2){
                    input.close();
                    return;
                }
                if(line[0].equals("tournament_name")){
                    tournament.setName(line[1].trim());
                }
                else if(line[0].equals("number_of_rounds")){
                    try {
                        tournament.setTotalRounds(Integer.parseInt(line[1].trim()));
                    } catch (Exception e) {
                        input.close();
                        return;
                    }
                }
                else if(line[0].equals("data_in_format")){
                    if(line[1].trim().equals("CSV"))
                        dataInType = CSV;
                }
                else if(line[0].equals("data_out_format")){
                    if(line[1].trim().equals("CSV"))
                        dataOutType = CSV;
                }
                else{
                    input.close();
                    return;
                }
            }
            next = input.readLine();
        }

        input.close();
    }
    private static File getFileIfExists(File tournamentFolder){
        if(tournamentFolder.isFile())
            return null;
        File configFile = new File(tournamentFolder.toPath() + "/config.txt");
        if(!configFile.exists())
            return null;
        return configFile;
    }
}
