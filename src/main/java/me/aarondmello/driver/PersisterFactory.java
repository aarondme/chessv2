package me.aarondmello.driver;


import me.aarondmello.csv.CsvReader;
import me.aarondmello.csv.CsvWriter;

public class PersisterFactory {
   public DataReader getReaderOfType(String type){
       return new CsvReader();
   }
   public DataWriter getWriterOfType(String type){
       return new CsvWriter();
   }
}
