package me.aarondmello.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import me.aarondmello.datatypes.Colour;
import me.aarondmello.datatypes.Division;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.PlayerGameSummary;
import me.aarondmello.datatypes.Tournament;
import me.aarondmello.datatypes.TiebreakType;

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
            reader.readLine();
            String header;
            while((header = reader.readLine()) != null)
                readDivisions(reader, t, header);
        }catch (IOException e){
            return null;
        }
        return t;
    }

    private void readDivisions(BufferedReader reader, Tournament t, String header) throws IOException {
        Division d = readDivisionName(header, t);
        readDivisionTiebreaks(reader, d, t.getRoundNumber());
        ArrayList<Player> p = readPlayers(reader, t.getRoundNumber());
        d.addPlayers(p);
        d.initialize();
    }

    private ArrayList<Player> readPlayers(BufferedReader reader, int roundNum) throws IOException {
        String line;
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<String[]> encoded_games = new ArrayList<>();
        while ((line = reader.readLine()) != null && !line.equals("")) {
            String[] splitLine = line.split(",");
            Player p = new Player(splitLine[1], splitLine[2]);
            p.setID(Integer.parseInt(splitLine[0]));
            players.add(p);
            encoded_games.add(splitLine);
        }
        for(int i = 0; i < players.size(); i++){
            Player p = players.get(i);
            String[] games = encoded_games.get(i);
            for(int j = 4; j < 4 + roundNum - 1; j++){
                int pointsEarned = (games[j].charAt(0) == 'W')? 2 : (games[j].charAt(0) == 'L')? 0 : 1;
                Colour colour = (games[j].charAt(1) == 'w')? Colour.WHITE : Colour.BLACK;
                Player opponent = players.get(Integer.parseInt(String.valueOf(games[j].charAt(2))));
                p.addPlayerGameSummary(new PlayerGameSummary(pointsEarned, opponent, colour));
            }
        }
        return players;
    }

    private void readDivisionTiebreaks(BufferedReader reader, Division d, int roundNum) throws IOException {
        String[] row = reader.readLine().split(",");
        ArrayList<TiebreakType> tb = new ArrayList<>();
        for(int i = 4 + roundNum - 1; i < row.length; i++){
            tb.add(TiebreakType.valueOf(row[i]));
        }
        TiebreakType[] a = new TiebreakType[tb.size()];
        tb.toArray(a);
        d.setTiebreaks(a);
    }

    private Division readDivisionName(String row, Tournament t){
        int firstSpace = row.indexOf(' ');
        return t.getDivisionWithName(row.substring(firstSpace+1), true);
    }

    private void readRounds(BufferedReader reader, Tournament t) throws IOException {
        String[] row = reader.readLine().split("\\s+");
        if(row[0].equals("Round")){
            t.setRoundNumber(Integer.parseInt(row[1]));
            t.setTotalRounds(Integer.parseInt(row[3]));
        }else{
            int x = Integer.parseInt(row[0].split("-")[0]);
            t.setRoundNumber(x + 1);
            t.setTotalRounds(x);
        }


    }

    private void readName(BufferedReader reader, Tournament t) throws IOException {
        String row = reader.readLine();
        int firstComma = row.indexOf(',');
        t.setName(row.substring(firstComma+1));
    }
}
