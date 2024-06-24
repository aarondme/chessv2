package me.aarondmello.csv;

import me.aarondmello.datatypes.*;
import me.aarondmello.driver.DataReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CsvReader implements DataReader{
    String organizationName;
    Map<String, ArrayList<Player>> divisionToPlayerList;
    public CsvReader(){}

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

    public Tournament readFromInProgressFile(BufferedReader reader) {

        try {
            String name = readName(reader);
            boolean isRegionalTournament = readIsRegionalTournament(reader);
            int[] rounds = readRounds(reader);
            Tournament t = new Tournament(name, rounds[1], isRegionalTournament);
            t.setRoundNumber(rounds[0]);
            reader.readLine();
            String header;
            while((header = reader.readLine()) != null)
                readDivisions(reader, t, header);
            return t;
        }catch (IOException e){
            return null;
        }
    }

    @Override
    public void readRoundResults(Tournament tournament, String fileName) {
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

    @Override
    public void readRoundResults(Tournament t) {
        readRoundResults(t, String.format("%s_Round %d_Pairing.csv", t.getName(), t.getRoundNumber()));
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

    private void readDivisions(BufferedReader reader, Tournament t, String header) throws IOException {
        Division d = readDivisionName(header, t);
        readDivisionTiebreaks(reader, d, t.getRoundNumber());
        ArrayList<Player> p = readPlayers(reader, t.getRoundNumber());
        d.addPlayers(p, true);
        d.initialize();
    }

    private ArrayList<Player> readPlayers(BufferedReader reader, int roundNum) throws IOException {
        String line;
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<String[]> encoded_games = new ArrayList<>();
        while ((line = reader.readLine()) != null && line.matches("(.*)[0-9](.*)")) {
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

                Player opponent = p; //TODO this is here just to make compiler happy, fix
                int index = Integer.parseInt(games[j].substring(2));
                if(index >= 0) {
                    for (Player o : players) {
                        if (index == o.getID())
                            opponent = o;
                    }
                }
                else opponent = NullPlayer.getInstance();
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
        String[] cells = row.split(",");
        int firstSpace = cells[0].indexOf(" ");
        return t.getDivisionWithName(cells[0].substring(firstSpace+1), true);
    }

    private int[] readRounds(BufferedReader reader) throws IOException {
        String[] row = reader.readLine().split(",")[0].split("\\s+");
        if(row[0].equals("Round")){
            return new int[]{Integer.parseInt(row[1]), Integer.parseInt(row[3])};

        }else{
            int x = Integer.parseInt(row[0].split("-")[0]);
            return new int[]{x+1, x};
        }


    }

    private String readName(BufferedReader reader) throws IOException {
        return reader.readLine().split(",")[1];
    }

    private boolean readIsRegionalTournament(BufferedReader reader) throws IOException{
        return Boolean.parseBoolean(reader.readLine().split(",")[1]);
    }

    @Override
    public void readFromStarterFile(BufferedReader reader, Tournament tournament) throws IOException {
        divisionToPlayerList = new HashMap<>();

        organizationName = reader.readLine().split(",")[0].strip();
        int start = 0;
        while (start < 5 && organizationName.charAt(start) >= 128) {
            start++;
        } //Removes BOM from UTF-8/UTF-16 encoded CSVs
        organizationName = organizationName.substring(start);


        reader.readLine();
        String row = reader.readLine();
        do {
            String[] splitRow = row.split(",");
            addPlayerToList(splitRow[0].trim(), splitRow[1].trim());
        } while ((row = reader.readLine()) != null);

        tournament.addPlayers(divisionToPlayerList);
    }

}
