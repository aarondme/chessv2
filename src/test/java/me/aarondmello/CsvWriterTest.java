package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import me.aarondmello.csvwriter.CsvWriter;
import me.aarondmello.csvwriter.CsvWriter.InvalidFileException;

public class CsvWriterTest {
    static String pathToDataFolder = "./src/test/data/";
    
    File file;
    CsvWriter csvWriter;

    public void initCsvWriter(String fromDataFolderToFile){
        file = new File(pathToDataFolder + fromDataFolderToFile);
        csvWriter = new CsvWriter(file);
    }

    @Test
    public void throwsErrorOnEmpty(){
        initCsvWriter("Empty CSV Test/empty.csv");
        assertThrows(InvalidFileException.class,  () -> csvWriter.checkFile());
    }

    @Test
    public void throwsErrorWhenFileNotFound(){
        initCsvWriter("nonexistantFile.csv");
        assertThrows(FileNotFoundException.class, () -> csvWriter.checkFile());
    }
}
