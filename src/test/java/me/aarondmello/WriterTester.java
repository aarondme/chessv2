package me.aarondmello;

import me.aarondmello.constants.GameResult;
import me.aarondmello.csv.CsvWriter;
import me.aarondmello.datatypes.*;
import me.aarondmello.tiebreaks.TiebreakType;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WriterTester {
    CsvWriter writer = new CsvWriter();
    String root = "src/test/data/Save Files/";
    String name;
    String expected;
    String actual;
    PrintWriter out;

    public WriterTester() {
    }
    private void setup(String r) throws IOException {
        name = r;
        expected = root + name + ".csv";
        actual = root + name + "_actual.csv";
        out = new PrintWriter(new FileWriter(actual));
    }

    @Test
    public void tournamentWithNoDivisions() throws IOException {
        setup("tournament_with_no_divisions");

        Tournament t = TournamentBuilder.createTournament()
                    .withName("myTournament")
                    .withNRounds(5)
                    .execute();

        writer.saveTournament(t, out);
        out.close();

        long mismatch = filesCompareByLine(Path.of(expected), Path.of(actual));
        assertEquals(-1, mismatch);
        Files.delete(Path.of(actual));
    }


    @Test
    public void tournamentWithOneEmptyDivision() throws IOException {
        setup("tournament_with_one_empty_division");
        Tournament t = TournamentBuilder.createTournament()
                .withName("tournament")
                .withNRounds(7)
                .withDivisionTiebreaks("a", new TiebreakType[]{})
                .execute();

        writer.saveTournament(t, out);
        out.close();

        long mismatch = filesCompareByLine(Path.of(expected), Path.of(actual));
        assertEquals(-1, mismatch);
        Files.delete(Path.of(actual));
    }

    @Test
    public void tournamentWithOneDivisionAndOnePlayerWithNoTiebreaksOrGames() throws IOException {
        setup("tournament_with_one_division_and_one_player_with_no_tiebreaks_or_games");
        Tournament t = TournamentBuilder.createTournament()
                .withName("myTournament")
                .withNRounds(3)
                .withPlayer("div1", new Player("player1", "org1"))
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .execute();

        writer.saveTournament(t, out);
        out.close();

        long mismatch = filesCompareByLine(Path.of(expected), Path.of(actual));
        assertEquals(-1, mismatch);
        Files.delete(Path.of(actual));
    }

    @Test
    public void tournamentWithOneDivisionAndOnePlayerWithSomeTiebreaksNoGames() throws IOException {
        setup("tournament_with_one_division_and_one_player_with_some_tiebreaks_no_games");
        Tournament t = TournamentBuilder.createTournament()
                .withName("myTournament")
                .withNRounds(2)
                .withPlayer("div2", new Player("player", "org"))
                .execute();

        Division division = t.getDivisionWithName("div2", false);
        division.initialize();

        writer.saveTournament(t, out);
        out.close();

        long mismatch = filesCompareByLine(Path.of(expected), Path.of(actual));
        assertEquals(-1, mismatch);
        Files.delete(Path.of(actual));
    }

    @Test
    public void tournamentWithTwoDivisionsAndOnePlayerInEach() throws IOException {
        setup("tournament_with_two_divisions_and_one_player_in_each");
        Tournament t = TournamentBuilder.createTournament()
                .withName("myTournament")
                .withNRounds(3)
                .withPlayer("div1", new Player("p1", "org"))
                .withPlayer("div2", new Player("p2", "org"))
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .withDivisionTiebreaks("div2", new TiebreakType[]{})
                .execute();

        writer.saveTournament(t, out);
        out.close();

        long mismatch = filesCompareByLine(Path.of(expected), Path.of(actual));
        assertEquals(-1, mismatch);
        Files.delete(Path.of(actual));
    }

    @Test
    public void tournamentWithOneDivisionAndOneGame() throws IOException {
        setup("tournament_with_one_division_and_one_game");
        Player p1 = new Player("p1", "org");
        Player p2 =  new Player("p2", "org");

        Round r = new Round();
        Game g = new Game(p1, p2);
        r.addGame(g);
        g.setResult(GameResult.WHITE_WIN);

        Tournament t = TournamentBuilder.createTournament()
                .withName("tournament")
                .withNRounds(3)
                .withPlayer("div1", p1)
                .withPlayer("div1", p2)
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .withRound("div1", r)
                .execute();
        p1.setID(1);
        p2.setID(2);

        writer.saveTournament(t, out);
        out.close();

        long mismatch = filesCompareByLine(Path.of(expected), Path.of(actual));
        assertEquals(-1, mismatch);
        Files.delete(Path.of(actual));
    }

    @Test
    public void completeTournamentWithOneDivision() throws IOException {
        setup("complete_tournament_with_one_division");
        Player p1 = new Player("p1", "org");
        Player p2 =  new Player("p2", "org");

        Round r = new Round();
        Game g = new Game(p1, p2);
        r.addGame(g);
        g.setResult(GameResult.WHITE_WIN);

        Tournament t = TournamentBuilder.createTournament()
                .withName("tournament")
                .withNRounds(1)
                .withPlayer("div1", p1)
                .withPlayer("div1", p2)
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .withRound("div1", r)
                .execute();
        p1.setID(1);
        p2.setID(2);

        writer.saveTournament(t, out);
        out.close();

        long mismatch = filesCompareByLine(Path.of(expected), Path.of(actual));
        assertEquals(-1, mismatch);
        Files.delete(Path.of(actual));
    }

    public static long filesCompareByLine(Path path1, Path path2) throws IOException {
        try (BufferedReader bf1 = Files.newBufferedReader(path1);
             BufferedReader bf2 = Files.newBufferedReader(path2)) {

            long lineNumber = 1;
            String line1, line2;
            while ((line1 = bf1.readLine()) != null) {
                line2 = bf2.readLine();
                if (!line1.equals(line2)) {
                    return lineNumber;
                }
                lineNumber++;
            }
            if (bf2.readLine() == null) {
                return -1;
            }
            else {
                return lineNumber;
            }
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

        public TournamentBuilder withRound(String division, Round r) {
            Division d = t.getDivisionWithName(division, true);
            t.setRoundNumber(t.getRoundNumber() + 1);
            d.setCurrentRound(r);
            d.confirmRoundResults();
            return this;
        }
    }
}
