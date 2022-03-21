package me.aarondmello;

import me.aarondmello.csv.CsvReader;
import me.aarondmello.datatypes.Division;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Tournament;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ResumeTournamentTest {
    CsvReader csvReader = new CsvReader();
    BufferedReader reader = Mockito.mock(BufferedReader.class);

    @Test
    public void tournamentWithNoDivisions() throws IOException {
        when(reader.readLine()).thenReturn("Tournament Name:,myTournament")
                        .thenReturn("Round 1 of 5")
                        .thenReturn(null);

        Tournament t = csvReader.resumeTournament(reader);

        assertEquals("myTournament", t.getName());
        assertEquals(1, t.getRoundNumber());
        assertEquals(5, t.getTotalRounds());
    }

    @Test
    public void tournamentWithOneEmptyDivision() throws IOException{
        when(reader.readLine()).thenReturn("Tournament Name:,tournament")
                .thenReturn("Round 1 of 7")
                .thenReturn("")
                .thenReturn("Division a")
                .thenReturn("ID,Name,Organization,Score")
                .thenReturn(null);

        Tournament t = csvReader.resumeTournament(reader);
        Division d = t.getDivisionWithName("a", false);

        assertEquals("tournament",t.getName());
        assertEquals(1, t.getRoundNumber());
        assertEquals(7, t.getTotalRounds());
        assertNotNull(d);
        assertEquals("",d.getTiebreaks());
    }
}
