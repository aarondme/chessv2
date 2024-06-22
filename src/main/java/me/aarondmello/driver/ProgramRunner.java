package me.aarondmello.driver;

import me.aarondmello.commandlineinterface.BasicProgramFlow;
import me.aarondmello.commandlineinterface.CsvInterface;
import me.aarondmello.csv.CsvReader;
import me.aarondmello.csv.CsvWriter;

public class ProgramRunner{
//    public static void main(String[] args) {
//        GUI gui = new BasicProgramFlow(new CommandLineInterface(new Scanner(System.in)));
//        gui.start(new CsvReader(), new CsvWriter());
//    }
public static void main(String[] args) {
    GUI gui = new BasicProgramFlow(new CsvInterface());
    gui.start(new CsvReader(), new CsvWriter());
}
}