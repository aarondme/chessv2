package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import me.aarondmello.csv.CsvReader;
import me.aarondmello.datatypes.Player;

public class CommonOrganizationTest {
    BufferedReader reader = Mockito.mock(BufferedReader.class);
    CsvReader csvReader = new CsvReader(reader);

    @ParameterizedTest
    @ValueSource(strings = {"testOrganization","otherOrganization"})
    public void fileWithOnePlayerReturnsListWithOnePlayerWithOrganizationName(String orgName) throws IOException{
        when(reader.readLine()).thenReturn(orgName)
                                .thenReturn("Player,Division")
                                .thenReturn("abc,firstdivision")
                                .thenReturn(null);
        
        csvReader.read();
        ArrayList<Player> playerList = csvReader.getDivisionList("firstdivision");

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
        
        csvReader.read();
        ArrayList<Player> playerList = csvReader.getDivisionList("firstdivision");

        assertEquals(1, playerList.size());
        assertEquals(playerName, playerList.get(0).getName());
    }

    @Test
    public void fileWithOnePlayerReturnsListInCorrectDivision() throws IOException{
        when(reader.readLine()).thenReturn("testOrganization")
                                .thenReturn("Player,Division")
                                .thenReturn("def,divisionone")
                                .thenReturn(null);
        
        csvReader.read();

        assertEquals(1, csvReader.getDivisionList("divisionone").size());
        assertNull(csvReader.getDivisionList("firstdivision"));
    }

    @Test
    public void fileWithTwoPlayersInSameDivisionReturnsBothPlayersInSameDivision() throws IOException{
        when(reader.readLine()).thenReturn("testOrganization")
                                .thenReturn("Player,Division")
                                .thenReturn("def,divisionone")
                                .thenReturn("ghi,divisionone")
                                .thenReturn(null);
        
        csvReader.read();
        ArrayList<Player> playerList = csvReader.getDivisionList("divisionone");

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
        
        csvReader.read();
        ArrayList<Player> divisionOnePlayers = csvReader.getDivisionList("divisionone");
        ArrayList<Player> divisionTwoPlayers = csvReader.getDivisionList("divisiontwo");

        assertEquals(1, divisionOnePlayers.size());
        assertEquals("def", divisionOnePlayers.get(0).getName());
        assertEquals(1, divisionTwoPlayers.size());
        assertEquals("ghi", divisionTwoPlayers.get(0).getName());
    }
}
