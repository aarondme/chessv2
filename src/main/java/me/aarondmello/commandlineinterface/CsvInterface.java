package me.aarondmello.commandlineinterface;

import me.aarondmello.datatypes.*;
import me.aarondmello.driver.DataReader;
import me.aarondmello.driver.DataWriter;
import me.aarondmello.driver.GUI;
import me.aarondmello.swinguserinterface.NewOrResumeTournamentPanel;
import me.aarondmello.swinguserinterface.NewTournamentPanel;
import me.aarondmello.swinguserinterface.TournamentFolderPanel;
import me.aarondmello.swinguserinterface.WelcomePanel;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static javax.swing.JOptionPane.*;

public class CsvInterface implements BasicPrompts {

    @Override
    public void displayWelcomeMessage() {
        String prompt = """
                --- Chess Tournament Manager by Aaron D'Mello---
                Licensed under CC BY-SA 4.0
                http://creativecommons.org/licenses/by-sa/4.0/?ref=chooser-v1""";
        String title = "Chess Tournament Manager";

        showMessageDialog(null, prompt, title, INFORMATION_MESSAGE);
    }

    @Override
    public boolean getIfStartingNewTournament() {
        String[] options = { "New Tournament", "Resume Tournament" };
        String prompt = "Choose whether to create a new tournament or to resume an existing tournament";
        String title = "Chess Tournament Manager";

        return showOptionDialog(null, prompt, title, YES_NO_CANCEL_OPTION,
                PLAIN_MESSAGE, null, options, options[0]) == OK_OPTION;
    }

    @Override
    public File getLocationOfExistingTournament() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int option = fileChooser.showOpenDialog(null);

        if(option == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();
        return null;
    }

    @Override
    public Tournament editNewTournamentDetails(Tournament t, DataReader dataReader) {
        NewTournamentPanel newTournamentPanel = new NewTournamentPanel();
        int input = newTournamentPanel.promptForValidNewTournamentData();

        if(input == NewTournamentPanel.VALID_INPUT_ENTERED)
            newTournamentPanel.createNewTournament(t);

        return t;
    }

    @Override
    public void alterPlayersSittingOut(Tournament t) {

    }

    @Override
    public void getRoundResults(Tournament t, DataReader reader) {
        String prompt = "Press confirm when the csv is filled";
        String title = "Chess Tournament Manager";

        do {
            showMessageDialog(null, prompt, title, INFORMATION_MESSAGE);

            reader.readRoundResults(t, fileNameForTournament(t)); //Extract from save
        }
        while(!t.confirmRoundResults());

    }

    private String fileNameForTournament(Tournament tournament){
        return String.format("%s_Round %d_Pairing.csv", tournament.getName(), tournament.getRoundNumber());
    }


    @Override
    public void displayStandings(Tournament t) {

    }

    @Override
    public void displayFileSaveError() {
        String prompt = "Tournament failed to save";
        String title = "Chess Tournament Manager";

        showMessageDialog(null, prompt, title, INFORMATION_MESSAGE);
    }
}
