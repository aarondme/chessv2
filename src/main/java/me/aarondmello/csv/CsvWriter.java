package me.aarondmello.csv;

import me.aarondmello.constants.Colour;
import me.aarondmello.datatypes.Division;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.PlayerGameSummary;
import me.aarondmello.datatypes.Tournament;
import me.aarondmello.tiebreaks.Tiebreak;

import java.io.PrintWriter;

public class CsvWriter {

    public void saveTournament(Tournament tournament, PrintWriter printWriter) {
        printWriter.println("Tournament Name:," + tournament.getName());
        printWriter.println("Round " + tournament.getRoundNumber() + " of " + tournament.getTotalRounds());
        for(Division division : tournament.getDivisions()){
            saveDivision(division, printWriter, tournament.getRoundNumber());
        }
    }

    private void saveDivision(Division division, PrintWriter printWriter, int numRounds) {
        printWriter.println("");
        printWriter.println("Division " + division.getName());
        saveHeader(division, printWriter, numRounds);

        for(Player p : division.getPlayers()){
            savePlayer(p, printWriter);
        }
    }

    private void saveHeader(Division division, PrintWriter printWriter, int numRounds) {
        printWriter.print("ID,Name,Organization,Score");
        for(int i = 1; i < numRounds; i++){
            printWriter.print(",Game " + i);
        }
        printWriter.print(division.getTiebreaks());
        printWriter.print("\n");
    }

    private void savePlayer(Player p, PrintWriter printWriter) {
        printWriter.print(p.getID());
        printWriter.print("," + p.getName());
        printWriter.print("," + p.getOrganization());
        printWriter.print("," + p.getScore());
        for(PlayerGameSummary g: p.getPlayerGameSummarys()){
            saveGame(g, printWriter);
        }
        for (Tiebreak t : p.getTiebreaks()){
            printWriter.print("," + t.getScore());
        }
        printWriter.print("\n");
    }

    private void saveGame(PlayerGameSummary g, PrintWriter printWriter) {
        int score = g.getPointsEarned();
        int colour = g.getColour();
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
