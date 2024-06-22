package me.aarondmello.driver;

import me.aarondmello.commandlineinterface.BasicProgramFlow;
import me.aarondmello.commandlineinterface.CommandLineInterface;
import me.aarondmello.commandlineinterface.CsvInterface;
import me.aarondmello.csv.CsvReader;
import me.aarondmello.csv.CsvWriter;

import java.util.Scanner;

public class ProgramRunner{

public static void main(String[] args) {
    BasicProgramFlow basicProgramFlow;
    if(args.length == 0 || !args[0].equals("-c"))
        basicProgramFlow = new BasicProgramFlow(new CsvInterface());
    else
        basicProgramFlow = new BasicProgramFlow(new CommandLineInterface(new Scanner(System.in)));
    basicProgramFlow.start(new CsvReader(), new CsvWriter());
}
}