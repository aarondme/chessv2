package me.aarondmello;

import me.aarondmello.csv.CsvReader;
import me.aarondmello.datatypes.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ResumeTournamentTest {
    TournamentComparer c = new TournamentComparer();
    CsvReader csvReader = new CsvReader();
    String root = "src/test/data/Save Files/";
    BufferedReader reader;

    @Test
    public void tournamentWithNoDivisions() throws IOException {
        String name = "tournament_with_no_divisions.csv";
        reader = new BufferedReader(new FileReader(root + name));

        Tournament t = csvReader.resumeTournament(reader);
        Tournament v = TournamentBuilder.createTournament()
                        .withName("myTournament")
                        .withNRounds(5)
                        .execute();

        assertEquals(TournamentComparer.areEqual, c.compare(t,v));
    }

    @Test
    public void tournamentWithOneEmptyDivision() throws IOException{
        String name = "tournament_with_one_empty_division.csv";
        reader = new BufferedReader(new FileReader(root + name));

        Tournament t = csvReader.resumeTournament(reader);
        Tournament v = TournamentBuilder.createTournament()
                .withName("tournament")
                .withNRounds(7)
                .withDivisionTiebreaks("a", new TiebreakType[]{})
                .execute();

        assertEquals(TournamentComparer.areEqual, c.compare(t, v));
    }

    @Test
    public void tournamentWithOneDivisionAndOnePlayerWithNoTiebreaksOrGames() throws IOException{
        String name = "tournament_with_one_division_and_one_player_with_no_tiebreaks_or_games.csv";
        reader = new BufferedReader(new FileReader(root+name));

        Tournament t = csvReader.resumeTournament(reader);
        Tournament v = TournamentBuilder.createTournament()
                .withName("myTournament")
                .withNRounds(3)
                .withPlayer("div1", new Player("player1", "org1"))
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .execute();

        assertEquals(TournamentComparer.areEqual, c.compare(t, v));
    }

    @Test
    public void tournamentWithOneDivisionAndOnePlayerWithSomeTiebreaksNoGames() throws FileNotFoundException {
        String name = "tournament_with_one_division_and_one_player_with_some_tiebreaks_no_games.csv";
        reader = new BufferedReader(new FileReader(root+name));

        Tournament t = csvReader.resumeTournament(reader);

        TiebreakType[] defaultTiebreaks = {TiebreakType.BuchholzCutOne, TiebreakType.Buchholz,
                TiebreakType.SonnebornBerger, TiebreakType.ProgressiveScores,
                TiebreakType.DirectEncounter, TiebreakType.WinCount,
                TiebreakType.WinCountAsBlack};
        Tournament v = TournamentBuilder.createTournament()
                        .withName("myTournament")
                        .withNRounds(2)
                        .withPlayer("div2", new Player("player", "org"))
                        .withDivisionTiebreaks("div2", defaultTiebreaks)
                        .execute();

        assertEquals(TournamentComparer.areEqual, c.compare(t, v));
    }

    @Test
    public void tournamentWithTwoDivisionsAndOnePlayerInEach() throws IOException {
        String name = "tournament_with_two_divisions_and_one_player_in_each.csv";
        reader = new BufferedReader(new FileReader(root+name));
        Tournament t = csvReader.resumeTournament(reader);
        Tournament v = TournamentBuilder.createTournament()
                .withName("myTournament")
                .withNRounds(3)
                .withPlayer("div1", new Player("p1", "org"))
                .withPlayer("div2", new Player("p2", "org"))
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .withDivisionTiebreaks("div2", new TiebreakType[]{})
                .execute();

        assertEquals(TournamentComparer.areEqual, c.compare(t, v));
    }

    @Test
    public void completeTournamentWithOneDivisionAndTiebreaks() throws IOException {
        String name = "complete_tournament_with_one_division_and_tiebreaks.csv";
        reader = new BufferedReader(new FileReader(root+name));
        Tournament t = csvReader.resumeTournament(reader);

        Player p1 = new Player("p1", "org");
        Player p2 =  new Player("p2", "org");

        Round r = new Round();
        Game g = new Game(p1, p2);
        r.addGame(g);
        g.setResult(GameResult.WHITE_WIN);

        Tournament v = TournamentBuilder.createTournament()
                .withName("tournament")
                .withNRounds(1)
                .withPlayer("div1", p1)
                .withPlayer("div1", p2)
                .withDivisionTiebreaks("div1", new TiebreakType[]{TiebreakType.WinCount})
                .withRound("div1", r)
                .execute();
        p1.setID(0);
        p2.setID(1);

        assertEquals(TournamentComparer.areEqual, c.compare(t, v));
    }
    @Test
    public void tournamentWithOneDivisionAndOneGame() throws FileNotFoundException{
        String name = "tournament_with_one_division_and_one_game.csv";
        reader = new BufferedReader(new FileReader(root+name));

        Tournament t = csvReader.resumeTournament(reader);

        Player p1 = new Player("p1", "org");
        p1.setID(1);
        Player p2 = new Player("p2", "org");
        p2.setID(2);

        Round r = new Round();
        Game g = new Game(p1, p2);
        g.setResult(GameResult.WHITE_WIN);
        r.addGame(g);
        Tournament v = TournamentBuilder.createTournament()
                .withName("tournament")
                .withNRounds(3)
                .withPlayer("div1", p1)
                .withPlayer("div1", p2)
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .withRound("div1", r)
                .execute();

        assertEquals(TournamentComparer.areEqual, c.compare(t, v));
    }


    @Test
    public void completeTournamentWithOneDivision() throws FileNotFoundException {
        String name = "complete_tournament_with_one_division.csv";
        reader = new BufferedReader(new FileReader(root+name));
        Tournament t = csvReader.resumeTournament(reader);

        Player p1 = new Player("p1", "org");
        Player p2 =  new Player("p2", "org");

        Round r = new Round();
        Game g = new Game(p1, p2);
        r.addGame(g);
        g.setResult(GameResult.WHITE_WIN);

        Tournament v = TournamentBuilder.createTournament()
                .withName("tournament")
                .withNRounds(1)
                .withPlayer("div1", p1)
                .withPlayer("div1", p2)
                .withDivisionTiebreaks("div1", new TiebreakType[]{})
                .withRound("div1", r)
                .execute();
        p1.setID(0);
        p2.setID(1);

        assertEquals(TournamentComparer.areEqual, c.compare(t, v));
    }
}