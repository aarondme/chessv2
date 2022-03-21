package me.aarondmello.csv;

import me.aarondmello.datatypes.Division;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Tournament;
import me.aarondmello.tiebreaks.Tiebreak;

import java.io.PrintWriter;

public class CsvWriter {

    public void saveTournament(Tournament tournament, PrintWriter printWriter) {
        printWriter.println("Tournament Name:," + tournament.getName());
        printWriter.println("Round " + tournament.getRoundNumber() + " of " + tournament.getTotalRounds());
        for(Division division : tournament.getDivisions()){
            saveDivision(division, printWriter);
        }
    }

    private void saveDivision(Division division, PrintWriter printWriter) {
        printWriter.println("");
        printWriter.println("Division " + division.getName());
        saveHeader(division, printWriter);

        for(Player p : division.getPlayers()){
            savePlayer(p, printWriter);
        }
    }

    private void saveHeader(Division division, PrintWriter printWriter) {
        printWriter.print("ID,Name,Organization");
        //Number of Games.
        printWriter.print(",Score");
        printWriter.print(division.getTiebreaks());
        printWriter.print("\n");
    }

    private void savePlayer(Player p, PrintWriter printWriter) {
        printWriter.print(p.getID());
        printWriter.print("," + p.getName());
        printWriter.print("," + p.getOrganization());
        printWriter.print("," + p.getScore());
        for (Tiebreak t : p.getTiebreaks()){
            printWriter.print("," + t.getScore());
        }
        printWriter.print("\n");
    }
}
