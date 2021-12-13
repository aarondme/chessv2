package me.aarondmello.driver;

import me.aarondmello.swinguserinterface.SwingUserInterface;

import java.io.File;
import java.util.Iterator;

import me.aarondmello.commandlineinterface.CommandLineInterface;
import me.aarondmello.datatypes.Tournament;
import me.aarondmello.maininterfaces.*;

public class ProgramRunner implements PersisterFactory{
    public static void main(String[] args) {
        GUI gui = new CommandLineInterface();
        gui.start(new ProgramRunner());
    }
    @Override
    public Persister getPersister() {
        return new Persister();
    }
}