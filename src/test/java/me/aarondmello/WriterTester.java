package me.aarondmello;

import me.aarondmello.csv.CsvWriter;
import me.aarondmello.datatypes.Division;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Tournament;
import me.aarondmello.tiebreaks.TiebreakType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WriterTester {
    CsvWriter writer = new CsvWriter();
    TestablePrintWriter printWriter = new TestablePrintWriter();

    public WriterTester() throws FileNotFoundException {
    }

    @Test
    public void tournamentWithNoDivisions(){
        Tournament t = TournamentBuilder.createTournament()
                    .withName("myTournament")
                    .withNRounds(5)
                    .execute();

        writer.saveTournament(t, printWriter);

        assertEquals("Tournament Name:,myTournament\n" +
                "Round 1 of 5\n", printWriter.getAsString());
    }

    @Test
    public void tournamentWithOneEmptyDivision(){
        Tournament t = TournamentBuilder.createTournament()
                .withName("tournament")
                .withNRounds(7)
                .withDivisionTiebreaks("a", new TiebreakType[]{})
                .execute();

        writer.saveTournament(t, printWriter);

        assertEquals("Tournament Name:,tournament\n" +
                "Round 1 of 7\n" +
                "\nDivision a\n" +
                "ID,Name,Organization,Score\n", printWriter.getAsString());
    }

    @Test
    public void tournamentWithOneDivisionAndOnePlayerWithNoTiebreaksOrGames(){
        Tournament t = TournamentBuilder.createTournament()
                .withName("myTournament")
                .withNRounds(3)
                .withPlayer("div1", new Player("player1", "org1"))
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .execute();

        writer.saveTournament(t, printWriter);

        assertEquals("Tournament Name:,myTournament\n" +
                "Round 1 of 3\n" +
                "\nDivision div1\n" +
                "ID,Name,Organization,Score\n" +
                "0,player1,org1,0\n", printWriter.getAsString());
    }

    @Test
    public void tournamentWithOneDivisionAndOnePlayerWithSomeTiebreaksNoGames(){
        Tournament t = TournamentBuilder.createTournament()
                .withName("myTournament")
                .withNRounds(2)
                .withPlayer("div2", new Player("player", "org"))
                .execute();

        Division division = t.getDivisionWithName("div2", false);
        division.initialize();

        writer.saveTournament(t, printWriter);

        assertEquals("Tournament Name:,myTournament\n" +
                "Round 1 of 2\n"+
                "\nDivision div2\n" +
                "ID,Name,Organization,Score,BuchholzCutOne,Buchholz,SonnebornBerger,ProgressiveScores,DirectEncounter," +
                "WinCount,WinCountAsBlack\n" +
                "0,player,org,0,0,0,0,0,0,0,0\n", printWriter.getAsString());
    }

    @Test
    public void tournamentWithTwoDivisionsAndOnePlayerInEach(){
        Tournament t = TournamentBuilder.createTournament()
                .withName("myTournament")
                .withNRounds(3)
                .withPlayer("div1", new Player("p1", "org"))
                .withPlayer("div2", new Player("p2", "org"))
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .withDivisionTiebreaks("div2", new TiebreakType[]{})
                .execute();

        writer.saveTournament(t, printWriter);

        assertEquals("Tournament Name:,myTournament\n" +
                "Round 1 of 3\n" +
                "\nDivision div1\n" +
                "ID,Name,Organization,Score\n" +
                "0,p1,org,0\n" +
                "\nDivision div2\n" +
                "ID,Name,Organization,Score\n" +
                "0,p2,org,0\n", printWriter.getAsString());
    }

    @Test
    public void tournamentWithOneDivisionAndOneGame(){

    }

    private class TestablePrintWriter extends PrintWriter {
        String out = "";

        public TestablePrintWriter() throws FileNotFoundException {
            super(new File("src/test/java/me/aarondmello/WriterTester"));
        }

        @Override
        public void print(String s){
            out += s;
        }
        @Override
        public void print(int x){out += "" + x;}

        @Override
        public void println(String s){
            out += s + '\n';
        }

        public String getAsString(){
            return out;
        }
    }

    private static class TournamentBuilder {
        Tournament t;
        TournamentBuilder(){
            t = new Tournament();
        }
        public static TournamentBuilder createTournament(){
            return new TournamentBuilder();
        }

        public TournamentBuilder withName(String s){
            t.setName(s);
            return this;
        }

        public TournamentBuilder withNRounds(int n){
            t.setTotalRounds(n);
            return this;
        }

        public TournamentBuilder withPlayer(String division, Player player){
            t.addPlayer(division, player);
            return this;
        }

        public TournamentBuilder withDivisionTiebreaks(String division, TiebreakType[] tiebreaks){
            Division d = t.getDivisionWithName(division, true);
            d.setTiebreaks(tiebreaks);
            return this;
        }
        public Tournament execute(){
            return t;
        }
    }
}