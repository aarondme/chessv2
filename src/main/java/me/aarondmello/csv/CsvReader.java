package me.aarondmello.csv;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

import me.aarondmello.datatypes.Player;

public class CsvReader {
    BufferedReader reader;
    String organizationName;
    HashMap<String, ArrayList<Player>> divisionToPlayerList = new HashMap<>();
    public CsvReader(BufferedReader reader){
        this.reader = reader;
    }
    public void read(){
        try {
            organizationName = reader.readLine();
            reader.readLine();
            String row = reader.readLine();
            do {
                String[] splitRow = row.split(",");
                addPlayerToList(splitRow[0].trim(), splitRow[1].trim());
            } while ((row = reader.readLine()) != null);
        } catch (Exception e) {
            //TODO: handle exception
        }   
    }

    public ArrayList<Player> getDivisionList(String division) {
        return divisionToPlayerList.get(division);
    }
    private void addPlayerToList(String playerName, String division){
        ArrayList<Player> players;
        if(divisionToPlayerList.containsKey(division))
            players = divisionToPlayerList.get(division);
        else{
            players = new ArrayList<Player>();
            divisionToPlayerList.put(division, players);
        }
        players.add(new Player(playerName, organizationName));
    }
}
