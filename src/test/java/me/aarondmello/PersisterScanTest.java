package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import me.aarondmello.driver.Persister;

public class PersisterScanTest {
    Persister p = new Persister();
    @Test
    public void emptyReturnIfNoConfig(){
        File tournamentFolder = new File("src/test/data/Persister Tests/EmptyFolder");
        p.scanFolder(tournamentFolder);
        assertEquals(0, p.getNumberOfFilesRead());
    }
    @Test
    public void onlyEmptyConfigExists(){
        File tournamentFolder = new File("src/test/data/Persister Tests/FolderWithOnlyEmptyConfig");
        p.scanFolder(tournamentFolder);
        assertEquals(1, p.getNumberOfFilesRead());
        assertTrue(p.wasFileRead("config.txt"));
        assertTrue(p.wasFileReadSuccessfully("config.txt"));
    }
    @Test
    public void returnsNullOnNullFile(){
        assertNull(p.scanFolder(null));
    }
}
