package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import me.aarondmello.csvwriter.CsvWriter;
import me.aarondmello.csvwriter.CsvWriter.InvalidFileException;

public class CsvWriterTest {
    @Test
    public void throwsErrorOnEmpty(){
        File file = new File("./src/test/data/Invalid Format Tests/empty.csv");
        CsvWriter csvWriter = new CsvWriter(file);
        assertThrows(InvalidFileException.class,  () -> csvWriter.checkFile());
    }

    @Test
    public void throwsErrorWhenFileNotFound(){
        File file = new File("invalidFile.csv");
        CsvWriter csvWriter = new CsvWriter(file);
        assertThrows(FileNotFoundException.class, () -> csvWriter.checkFile());
    }
}
