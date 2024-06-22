package me.aarondmello;

import me.aarondmello.csv.CsvReader;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CommonOrganizationTest {
    BufferedReader reader = Mockito.mock(BufferedReader.class);
    CsvReader csvReader = new CsvReader();
    Tournament tournament = new Tournament("DefaultName", 5, true);

    @ParameterizedTest
    @ValueSource(strings = {"testOrganization","otherOrganization"})
    public void fileWithOnePlayerReturnsListWithOnePlayerWithOrganizationName(String orgName) throws IOException{
        when(reader.readLine()).thenReturn(orgName)
                                .thenReturn("Player,Division")
                                .thenReturn("abc,firstdivision")
                                .thenReturn(null);

        csvReader.readFromStarterFile(reader, tournament);
        assertEquals(1, tournament.getDivisions().size());

        List<Player> playerList = tournament.getDivisionWithName("firstdivision").getPlayers();

        assertEquals(1, playerList.size());
        assertEquals(orgName, playerList.get(0).getOrganization());
    }

    @ParameterizedTest
    @ValueSource(strings = {"name","otherName"})
    public void fileWithOnePlayerReturnsListWithOnePlayerWithName(String playerName) throws IOException{
        when(reader.readLine()).thenReturn("testOrganization")
                                .thenReturn("Player,Division")
                                .thenReturn(playerName + ",firstdivision")
                                .thenReturn(null);

        csvReader.readFromStarterFile(reader, tournament);
        assertEquals(1, tournament.getDivisions().size());

        List<Player> playerList = tournament.getDivisionWithName("firstdivision").getPlayers();

        assertEquals(1, playerList.size());
        assertEquals(playerName, playerList.get(0).getName());
    }

    @Test
    public void fileWithTwoPlayersInSameDivisionReturnsBothPlayersInSameDivision() throws IOException{
        when(reader.readLine()).thenReturn("testOrganization")
                                .thenReturn("Player,Division")
                                .thenReturn("def,divisionone")
                                .thenReturn("ghi,divisionone")
                                .thenReturn(null);

        csvReader.readFromStarterFile(reader, tournament);
        assertEquals(1, tournament.getDivisions().size());

        List<Player> playerList = tournament.getDivisionWithName("divisionone").getPlayers();

        assertEquals(2, playerList.size());
        assertEquals("def", playerList.get(0).getName());
        assertEquals("ghi", playerList.get(1).getName());
    }

    @Test
    public void fileWithTwoPlayersInDifferentDivisionsReturnsBothPlayersInDifferentDivisions() throws IOException{
        when(reader.readLine()).thenReturn("testOrganization")
                                .thenReturn("Player,Division")
                                .thenReturn("def,divisionone")
                                .thenReturn("ghi,divisiontwo")
                                .thenReturn(null);

        csvReader.readFromStarterFile(reader, tournament);
        assertEquals(2, tournament.getDivisions().size());

        List<Player> divisionOnePlayers = tournament.getDivisionWithName("divisionone").getPlayers();
        List<Player> divisionTwoPlayers = tournament.getDivisionWithName("divisiontwo").getPlayers();

        assertEquals(1, divisionOnePlayers.size());
        assertEquals("def", divisionOnePlayers.get(0).getName());
        assertEquals(1, divisionTwoPlayers.size());
        assertEquals("ghi", divisionTwoPlayers.get(0).getName());
    }
}
