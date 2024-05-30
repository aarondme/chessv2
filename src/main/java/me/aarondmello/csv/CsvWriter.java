package me.aarondmello.csv;

import me.aarondmello.datatypes.*;
import me.aarondmello.driver.DataWriter;

import java.io.IOException;
import java.io.PrintWriter;

public class CsvWriter implements DataWriter {

    public void saveTournament(Tournament tournament, PrintWriter printWriter) {
        printWriter.println("Tournament Name:," + tournament.getName());
        printWriter.println("Divisional Tournament:," + tournament.isRegionalTournament());

        if(tournament.getTotalRounds() < tournament.getRoundNumber())
            printWriter.println(tournament.getTotalRounds() + "-Round Tournament Complete");
        else
            printWriter.println("Round " + tournament.getRoundNumber() + " of " + tournament.getTotalRounds());

        for(Division division : tournament.getDivisions()){
            saveDivision(division, printWriter, tournament.getRoundNumber());
        }
    }

    @Override
    public void saveRound(Tournament tournament, PrintWriter writer) {
        writer.println(tournament.getName());
        writer.println(String.format("Round,%d", tournament.getRoundNumber()));
        for (Division d: tournament.getDivisions()) {
            writer.println(String.format("Division,%s", d.getName()));
            writer.println("Game ID,White,Black,Result");
            int i = 1;
            for (Game g :d.getPairing()) {
                writer.println(String.format("%d,%s,%s,%s",
                        i, formatPlayer(g.getWhitePlayer()), formatPlayer(g.getBlackPlayer()),
                        (g.getResult() == null)? "":g.getResult().toString().charAt(0)));
                i++;
            }
        }
    }

    private String formatPlayer(Player player) {
        return player.getName() + " (" + player.getOrganization() + ") [" + player.getScore() + "]";
    }

    private void saveDivision(Division division, PrintWriter printWriter, int numRounds) {
        printWriter.println("");
        printWriter.println("Division " + division.getName());
        saveHeader(division, printWriter, numRounds);

        for(Player p : division.getPlayers()){
            savePlayer(p, printWriter, division);
        }
    }

    private void saveHeader(Division division, PrintWriter printWriter, int numRounds) {
        printWriter.print("ID,Name,Organization,Score");
        for(int i = 1; i < numRounds; i++){
            printWriter.print(",Game " + i);
        }
        printWriter.print(getTiebreakAsString(division.getTiebreaks()));
        printWriter.print("\n");
    }

    private String getTiebreakAsString(Tiebreak[] tiebreaks) {
        StringBuilder out = new StringBuilder();
        for(Tiebreak t:tiebreaks){
            out.append(",");
            out.append(t.name());
        }
        return out.toString();
    }

    private void savePlayer(Player p, PrintWriter printWriter, Division division) {
        printWriter.print(p.getID());
        printWriter.print("," + p.getName());
        printWriter.print("," + p.getOrganization());
        printWriter.print("," + p.getScore());
        for(PlayerGameSummary g: p.getPlayerGameSummaries()){
            saveGame(g, printWriter);
        }
        for (Tiebreak t : division.getTiebreaks()){
            printWriter.print("," + p.getTiebreakScore(t.type()));
        }
        printWriter.print("\n");
    }

    private void saveGame(PlayerGameSummary g, PrintWriter printWriter) {
        int score = g.getPointsEarned();
        Colour colour = g.getColour();
        int opponentId = g.getOpponent().getID();

        printWriter.print(',');
        if(score == 2)
            printWriter.print('W');
        else if(score == 1)
            printWriter.print('D');
        else
            printWriter.print('L');

        if(colour == Colour.WHITE)
            printWriter.print('w');
        else
            printWriter.print('b');

        printWriter.print(opponentId);
    }
}
