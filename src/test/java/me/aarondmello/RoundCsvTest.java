package me.aarondmello;

import me.aarondmello.csv.CsvReader;
import me.aarondmello.csv.CsvWriter;
import me.aarondmello.datatypes.Game;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Round;
import me.aarondmello.datatypes.Tournament;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static me.aarondmello.WriterTester.filesCompareByLine;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoundCsvTest {
    CsvWriter writer = new CsvWriter();
    CsvReader reader = new CsvReader();
    String root = "src/test/data/Round Files/";
    String name;
    String expected;
    String actual;
    PrintWriter out;

    public RoundCsvTest() {
    }
    private void setupWriter(String r) throws IOException {
        name = r;
        expected = root + name + ".csv";
        actual = root + name + "_actual.csv";
        out = new PrintWriter(new FileWriter(actual));
    }

    private Tournament initializeBasicTournament(){
        Tournament t = new Tournament();
        t.setName("myTournament");
        Player[] divisionAPlayers = new Player[]{new Player("A", "a"),
                new Player("B", "b"),
                new Player("C", "c"),
                new Player("D", "d")};
        Player[] divisionBPlayers = new Player[]{new Player("E", "e"),
                new Player("F", "f"),
                new Player("G", "g"),
                new Player("H", "h")};
        HashMap<String, ArrayList<Player>> div = new HashMap<>();
        div.put("A", new ArrayList<>(Arrays.asList(divisionAPlayers)));
        div.put("B", new ArrayList<>(Arrays.asList(divisionBPlayers)));
        t.addPlayers(div);
        t.setTotalRounds(5);
        t.initialize(false);
        Round rA = new Round();
        rA.addGame(new Game(divisionAPlayers[0], divisionAPlayers[1]));
        rA.addGame(new Game(divisionAPlayers[2], divisionAPlayers[3]));
        Round rB = new Round();
        rB.addGame(new Game(divisionBPlayers[0], divisionBPlayers[1]));
        rB.addGame(new Game(divisionBPlayers[2], divisionBPlayers[3]));
        t.getDivisionWithName("A").setCurrentRound(rA);
        t.getDivisionWithName("B").setCurrentRound(rB);
        return t;
    }

    @Test
    public void basicPairingNoResults() throws IOException {
        setupWriter("basicPairingNoResults");
        Tournament t = initializeBasicTournament();
        writer.saveRound(t, out);
        out.close();

        long mismatch = filesCompareByLine(Path.of(expected), Path.of(actual));
        assertEquals(-1, mismatch);
        Files.delete(Path.of(actual));
    }

    @Test
    public void loadBasicPairingWithResults() {
        Tournament t = initializeBasicTournament();
        reader.readRoundResults(t, root + "basicPairingWithResults.csv");
        t.confirmRoundResults();
        assertEquals(2, t.getPlayer("A", 0).getScore());
        assertEquals(0, t.getPlayer("A", 1).getScore());
        assertEquals(1, t.getPlayer("A", 2).getScore());
        assertEquals(1, t.getPlayer("A", 3).getScore());
        assertEquals(2, t.getPlayer("B", 0).getScore());
        assertEquals(0, t.getPlayer("B", 1).getScore());
        assertEquals(0, t.getPlayer("B", 2).getScore());
        assertEquals(2, t.getPlayer("B", 3).getScore());
    }
}
