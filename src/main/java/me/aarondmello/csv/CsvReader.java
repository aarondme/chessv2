package me.aarondmello.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.aarondmello.datatypes.Division;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Tournament;
import me.aarondmello.tiebreaks.Tiebreak;
import me.aarondmello.tiebreaks.TiebreakType;

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
            players = new ArrayList<>();
            divisionToPlayerList.put(division, players);
        }
        players.add(new Player(playerName, organizationName));
    }

    public Tournament resumeTournament(BufferedReader reader) {
        Tournament t = new Tournament();
        try {
            readName(reader, t);
            readRounds(reader, t);
            if(reader.readLine() != null)
                readDivisions(reader, t);
        }catch (Exception e){
            return null;
        }
        return t;
    }

    private void readDivisions(BufferedReader reader, Tournament t) throws IOException {
        Division d = readDivisionName(reader, t);
        readDivisionTiebreaks(reader, d);
    }

    private void readDivisionTiebreaks(BufferedReader reader, Division d) throws IOException {
        String[] row = reader.readLine().split(",");
        ArrayList<TiebreakType> tb = new ArrayList<>();
        for(int i = 4; i < row.length; i++){
            tb.add(TiebreakType.valueOf(row[i]));
        }
        TiebreakType[] types = new TiebreakType[0];
        tb.toArray(types);
        d.setTiebreaks(types);
    }

    private Division readDivisionName(BufferedReader reader, Tournament t) throws IOException {
        String row = reader.readLine();
        int firstSpace = row.indexOf(' ');
        return t.getDivisionWithName(row.substring(firstSpace+1), true);
    }

    private void readRounds(BufferedReader reader, Tournament t) throws IOException {
        String[] row = reader.readLine().split("\\s+");
        t.setRoundNumber(Integer.parseInt(row[1]));
        t.setTotalRounds(Integer.parseInt(row[3]));
    }

    private void readName(BufferedReader reader, Tournament t) throws IOException {
        String row = reader.readLine();
        int firstComma = row.indexOf(',');
        t.setName(row.substring(firstComma+1));
    }
}
