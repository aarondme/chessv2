package me.aarondmello.csv;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;

import me.aarondmello.datatypes.Player;

public class CsvReader {
    String organizationName;
    HashMap<String, ArrayList<Player>> divisionToPlayerList;
    public CsvReader(){}
    public HashMap<String, ArrayList<Player>> read(BufferedReader reader){
        divisionToPlayerList = new HashMap<>();
        try {
            organizationName = reader.readLine();
            reader.readLine();
            String row = reader.readLine();
            do {
                String[] splitRow = row.split(",");
                addPlayerToList(splitRow[0].trim(), splitRow[1].trim());
            } while ((row = reader.readLine()) != null);
        } catch (Exception e) {
           return null;
        }
        return divisionToPlayerList;
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
