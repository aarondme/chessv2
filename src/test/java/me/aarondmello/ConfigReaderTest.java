package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import me.aarondmello.csv.CsvReader;
import me.aarondmello.csv.CsvWriter;
import me.aarondmello.datatypes.Tournament;
import me.aarondmello.maininterfaces.*;
import me.aarondmello.driver.ConfigReader;

public class ConfigReaderTest {
    static String pathToDataFolder = "./src/test/data/";
    Tournament test;
    LinkedList<DataReader> readers;
    LinkedList<DataWriter> writers;
    public void initTournament(String pathToFolder) throws IOException{
        test = null;
        readers = null;
        writers = null;
        ConfigReader.init(new File(pathToDataFolder + pathToFolder), test, readers, writers);
    }
    @Test
    public void returnsNullOnInvalidDirectory() throws IOException{
        initTournament("/Empty Config Test/config.txt");
        assertEquals(null, test);
    }
    @Test
    public void returnsNullIfConfigDoesNotExist() throws IOException{
        initTournament("/Empty CSV Test");
        assertEquals(null, test);
    }
    @Test
    public void returnsNullIfConfigEmpty() throws IOException{
        initTournament("/Empty Config Test");
        assertEquals(null, test);
    }
    @ParameterizedTest
    @ValueSource(strings = {"/Invalid Config Test/Test 1","/Invalid Config Test/Test 2"})
    public void returnsNullIfInvalidConfig(String string) throws IOException{
        initTournament(string);
        assertEquals(null, test);
    }

    @Test
    public void createsValidTournamentOnValidConfig() throws IOException{
        initTournament("/Valid Config Test");
        assertEquals("myTournament", test.getTournamentName());
        assertEquals(5, test.getTotalRounds());
        assertTrue(readers.getLast() instanceof CsvReader);
        assertTrue(writers.getLast() instanceof CsvWriter); 
    }

}
