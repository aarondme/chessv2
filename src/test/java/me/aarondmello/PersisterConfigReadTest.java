package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.aarondmello.driver.FileReadSummary;
import me.aarondmello.driver.Persister;

public class PersisterConfigReadTest {
    @Test
    public void emptyConfigGivesError(){
        Persister p = new Persister();
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        FileReadSummary summary = new FileReadSummary("config.txt"); 
        p.readConfig(reader, summary);
        assertTrue(summary.didErrorOccur());
    }
}
