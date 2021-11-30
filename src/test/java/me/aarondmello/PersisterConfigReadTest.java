package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.FileReadSummary;
import me.aarondmello.driver.Persister;

public class PersisterConfigReadTest {
    Persister p = new Persister();
    BufferedReader reader = Mockito.mock(BufferedReader.class);
    FileReadSummary summary = new FileReadSummary("config.txt");
    Tournament tournament;
    @Test
    public void emptyConfigGivesError() throws IOException{
        when(reader.readLine()).thenReturn(null); 
        tournament = p.readConfig(reader, summary);
        assertFalse(summary.didErrorOccur());
    }

    @Test
    public void invalidSettingGivesError() throws IOException{
        when(reader.readLine()).thenReturn("foo=bar")
                               .thenReturn(null);
        tournament = p.readConfig(reader, summary);
        assertTrue(summary.didErrorOccur());
    }

    @Test
    public void nameAndRoundsReturnsTournament() throws IOException{
        String tournamentName = "testTournament"; 
        int numberOfRounds = 5;
        when(reader.readLine()).thenReturn("tournament_name=" + tournamentName)
                               .thenReturn("number_of_rounds=" + numberOfRounds)
                               .thenReturn(null);
        tournament = p.readConfig(reader, summary);
        assertFalse(summary.didErrorOccur());
        assertEquals(tournamentName, tournament.getName());
        assertEquals(numberOfRounds, tournament.getTotalRounds());
    }

    @Test
    public void nameAndRoundsWithSpacesReturnsTournament() throws IOException{
        String tournamentName = "spaceyTournament";
        int numberOfRounds = 7;
        when(reader.readLine()).thenReturn(" tournament_name =   " + tournamentName)
                               .thenReturn("number_of_rounds= " + numberOfRounds)
                               .thenReturn(null);
        tournament = p.readConfig(reader, summary);
        assertFalse(summary.didErrorOccur());
        assertEquals(tournamentName, tournament.getName());
        assertEquals(numberOfRounds, tournament.getTotalRounds());
    }
}
