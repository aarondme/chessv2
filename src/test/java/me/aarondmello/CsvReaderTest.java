package me.aarondmello;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import me.aarondmello.csv.CsvReader;
import me.aarondmello.csv.CsvReader.InvalidFileException;

public class CsvReaderTest {
    static String pathToDataFolder = "./src/test/data/";
    
    File file;
    CsvReader csvWriter;

    public void initCsvWriter(String fromDataFolderToFile){
        file = new File(pathToDataFolder + fromDataFolderToFile);
        csvWriter = new CsvReader(file);
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
