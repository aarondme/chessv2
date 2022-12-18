package me.aarondmello.driver;

import me.aarondmello.commandlineinterface.CommandLineInterface;

public class ProgramRunner{
    public static void main(String[] args) {
        GUI gui = new CommandLineInterface();
        gui.start(new PersisterFactory());
    }
}