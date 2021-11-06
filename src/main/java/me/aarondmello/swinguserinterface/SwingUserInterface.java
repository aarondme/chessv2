package me.aarondmello.swinguserinterface;
import javax.swing.*;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.maininterfaces.GUI;

import java.awt.*;
import java.util.HashMap;

import java.io.*;

public class SwingUserInterface implements GUI{
    boolean wasCancelPressed = false;
    SwingPanel currentPanel;
    Tournament tournament;
    File tournamentFolder;
    @Override
    public void start(){
        setCurrentPanel(new WelcomePanel());
    }

    public void setCurrentPanel(SwingPanel panel){
        currentPanel = panel;
        currentPanel.run(this);
    }

    public boolean getIfNewTournament() {
        String[] options = { "New Tournament", "Resume Tournament" };
        String prompt = "Choose whether to create a new tournament or to resume an existing tournament";
        String title = "Chess tournament Manager";
    
        int input = JOptionPane.showOptionDialog(null, prompt, title, JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    
        if (input == JOptionPane.CLOSED_OPTION) {
          wasCancelPressed = true;
          return false;
        }
    
        return (input == JOptionPane.OK_OPTION);
    }

    public Tournament getNewTournament() {
        if (wasCancelPressed)
            return null;

        NewTournamentPanel panel = new NewTournamentPanel();
        int inputCode = panel.promptForValidNewTournamentData();
        if(inputCode == NewTournamentPanel.VALID_INPUT_ENTERED){
            Tournament tournament = panel.createNewTournament();
            return tournament;
        }
        else if(inputCode == NewTournamentPanel.EXIT_BUTTON_PRESSED){
            wasCancelPressed = true;
        }
        return null;
    }
    
    public Tournament getExistingTournament() {
        // TODO Auto-generated method stub
        return null;
    }

    public HashMap<String,int[]> getRoundResults(Tournament tournament) {
        //TODO make it so threads work
        return null;
    }

    public void displayResults(Tournament tournament) {
        if (wasCancelPressed)
            return;
        JTextArea textArea = new JTextArea(25, 0);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        //TODO Fix for multiple divisions.
        /** 
        for (int i = 0; i < players.size(); i++) {
            Player q = players.get(i);
            textArea
                .append(String.format("%-7s" + " %-35s" + " %-4s" + "\n", (i + 1) + ".", q.getDisplayName(), q.getScore()));
        }

        JScrollPane scrollPlane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null, scrollPlane,
            name + " - Results after " + (currentRound + 1) + " of " + numberOfRounds + " rounds",
            JOptionPane.PLAIN_MESSAGE);
        **/
    }

    public void readFolder(File folder) {
    }
}