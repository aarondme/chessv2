package me.aarondmello.driver;

import me.aarondmello.commandlineinterface.CommandLineInterface;
import me.aarondmello.commandlineinterface.CsvInterface;
import me.aarondmello.csv.CsvReader;
import me.aarondmello.csv.CsvWriter;

import java.util.Scanner;

public class ProgramRunner{
    public static void main(String[] args) {
        GUI gui = new CsvInterface(new Scanner(System.in));
        gui.start(new CsvReader(), new CsvWriter());
    }
}