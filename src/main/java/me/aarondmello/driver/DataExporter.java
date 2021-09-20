package me.aarondmello.driver;
public interface DataExporter{
    public void writeToFile();
    public Tournament readFromStarterFile();
    public Tournament readFromInProgressFine(int number);
}