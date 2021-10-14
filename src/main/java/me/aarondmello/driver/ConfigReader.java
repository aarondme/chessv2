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
    /**
     * Initializes all empty fields based on config data
     * @param tournamentFolder points to the folder containing tournament data
     * @param tournament tournament with no data
     * @param readers empty list of readers
     * @param writers empty list of writers
     * @throws IOException
     */
    public static void init(File tournamentFolder, Tournament tournament, LinkedList<DataReader> readers, LinkedList<DataWriter> writers) throws IOException{
        int[] dataTransferType = {-1, -1};
        readConfig(tournamentFolder, tournament, dataTransferType);
    }
    private static void readConfig(File tournamentFolder, Tournament tournament, int[] dataTransferType) throws IOException{            
        File configFile = getFileIfExists(tournamentFolder);
        if(configFile == null){
            setFieldsInvalid(tournament, dataTransferType);
            return;
        }

        BufferedReader input = new BufferedReader(new FileReader(configFile));
        String next = input.readLine();

        while(next != null){
            next = next.trim();
            if(next.length() > 0 && next.charAt(0) != '#'){
                String[] line = next.split("\\s+");
                if(line.length != 2){
                    setFieldsInvalid(tournament, dataTransferType);
                    input.close();
                    return;
                }
                if(line[0].equals("tournament_name:")){
                    tournament.setName(line[1].trim());
                }
                else if(line[0].equals("tournament_type:")){
                    tournament.setType(line[1].trim());
                }
                else if(line[0].equals("number_of_rounds:")){
                    try {
                        tournament.setTotalRounds(Integer.parseInt(line[1].trim()));
                    } catch (Exception e) {
                        setFieldsInvalid(tournament, dataTransferType);
                        input.close();
                        return;
                    }
                }
                else if(line[0].equals("data_in_format:")){
                    if(line[1].trim().equals("CSV"))
                        dataTransferType[0] = CSV;
                }
                else if(line[0].equals("data_out_format:")){
                    if(line[1].trim().equals("CSV"))
                        dataTransferType[1] = CSV;
                }
                else{
                    setFieldsInvalid(tournament, dataTransferType);
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
    private static void setFieldsInvalid(Tournament tournament, int[] dataTransferType){
        tournament.setName(null);
        tournament.setTotalRounds(-1);
        dataTransferType[0] = -1;
        dataTransferType[1] = -1;
    }
}
