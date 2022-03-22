package me.aarondmello;

import me.aarondmello.csv.CsvReader;
import me.aarondmello.datatypes.Division;
import me.aarondmello.datatypes.Tournament;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResumeTournamentTest {
    CsvReader csvReader = new CsvReader();
    String root = "src/test/data/Save Files/";
    BufferedReader reader;

    @Test
    public void tournamentWithNoDivisions() throws IOException {
        String name = "tournament_with_no_divisions.csv";
        reader = new BufferedReader(new FileReader(root + name));

        Tournament t = csvReader.resumeTournament(reader);

        assertEquals("myTournament", t.getName());
        assertEquals(1, t.getRoundNumber());
        assertEquals(5, t.getTotalRounds());
    }

    @Test
    public void tournamentWithOneEmptyDivision() throws IOException{
        String name = "tournament_with_one_empty_division.csv";
        reader = new BufferedReader(new FileReader(root + name));

        Tournament t = csvReader.resumeTournament(reader);
        Division d = t.getDivisionWithName("a", false);

        assertEquals("tournament",t.getName());
        assertEquals(1, t.getRoundNumber());
        assertEquals(7, t.getTotalRounds());
        assertNotNull(d);
        assertEquals("",d.getTiebreaks());
    }
}
