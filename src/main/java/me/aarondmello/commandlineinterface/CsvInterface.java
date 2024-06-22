package me.aarondmello.commandlineinterface;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.DataReader;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
        getTournamentConfig(t);
        getPlayerList(t, dataReader);

        t.initialize(true);
        return t;
    }

    private void getPlayerList(Tournament t, DataReader dataReader) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        StringBuilder fileNames = new StringBuilder();

        while (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            for (File f : fileChooser.getSelectedFiles()) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    dataReader.readFromStarterFile(reader, t);
                    fileNames.append(f.getName()).append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int option = showConfirmDialog(null, "Files added: " + fileNames + "Press confirm to continue, or cancel to add more files");
            if(option == OK_OPTION || option == CLOSED_OPTION)
                break;
        }
    }

    private void getTournamentConfig(Tournament t){
        JTextField tournamentName = new JTextField();
        JSpinner numRounds = new JSpinner();
        JCheckBox divisional = new JCheckBox();
        final JComponent[] inputs = new JComponent[] {
                new JLabel("Tournament Name"),
                tournamentName,
                new JLabel("Number of Rounds"),
                numRounds,
                new JLabel("Divisional Tournament?"),
                divisional
        };
        int result = JOptionPane.showConfirmDialog(null, inputs, "My custom dialog", DEFAULT_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            t.setRegionalTournament(divisional.isSelected());
            t.setTotalRounds((Integer) numRounds.getValue());
            t.setName(tournamentName.getText());
        } else {
            System.out.println("User canceled / closed the dialog, result = " + result);
        }
    }

    @Override
    public void alterPlayersSittingOut(Tournament t) {
        //TODO:
    }

    @Override
    public void getRoundResults(Tournament t, DataReader reader) {
        String prompt = "Press confirm when the csv is filled. Enter W for a white win, B for a black win, D for a draw";
        String title = "Chess Tournament Manager";

        do {
            showMessageDialog(null, prompt, title, INFORMATION_MESSAGE);

            reader.readRoundResults(t); //Extract from save
        }
        while(!t.confirmRoundResults());

    }

    @Override
    public void displayStandings(Tournament t) {
        String prompt = "Current Standings saved in csv";
        String title = "Chess Tournament Manager";

        showMessageDialog(null, prompt, title, INFORMATION_MESSAGE);
    }

    @Override
    public void displayFileSaveError() {
        String prompt = "Tournament failed to save";
        String title = "Chess Tournament Manager";

        showMessageDialog(null, prompt, title, INFORMATION_MESSAGE);
    }
}
