package me.aarondmello.csv;

import java.io.File;
import java.io.FileNotFoundException;

public class CsvReader {
    File file;
    public CsvReader(File file){
        this.file = file;
    }

    public class InvalidFileException extends Exception{
        
    }

    public void checkFile() throws InvalidFileException, FileNotFoundException {
        if(!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
        
        throw new InvalidFileException();
    }
}
