package me.aarondmello.csvwriter;

import java.io.File;
import java.io.FileNotFoundException;

public class CsvWriter {
    File file;
    public CsvWriter(File file){
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
