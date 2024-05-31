package me.aarondmello.swinguserinterface;
import javax.swing.*;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.driver.DataReader;
import me.aarondmello.driver.DataWriter;
import me.aarondmello.driver.GUI;

import java.awt.*;
import java.util.HashMap;

import java.io.*;

public class SwingUserInterface implements GUI{
    boolean wasCancelPressed = false;
    Tournament tournament;
    File tournamentFolder;
    DataReader tournamentReader;
    DataWriter tournamentWriter;
    WelcomePanel welcomePanel = new WelcomePanel();
    TournamentFolderPanel tournamentFolderPanel = new TournamentFolderPanel();
    NewOrResumeTournamentPanel newOrResumeTournamentPanel = new NewOrResumeTournamentPanel();
    NewTournamentPanel newTournamentpanel = new NewTournamentPanel();
    @Override
    public void start(DataReader tournamentReader, DataWriter tournamentWriter){
        this.tournamentReader = tournamentReader;
        this.tournamentWriter = tournamentWriter;
        welcomePanel.displayWelcomeMessage();
        getTournament();
        runTournament();
        saveTournament();
    }
    private void saveTournament() {
    }
    private void runTournament() {
    }
    private void getTournament(){
        if(wasCancelPressed)
            return;

        newOrResumeTournamentPanel.getIfStartingNewTournament();
//        if (input == JOptionPane.CLOSED_OPTION)
//            return;
        //else if(input == JOptionPane.OK_OPTION)
          //  this.tournament = tournamentManager.createTournament();
        //else
          //  this.tournament = tournamentManager.resumeTournament();
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
  
    public File getSaveLocation() {
        return tournamentFolder = tournamentFolderPanel.getFolder();
    }

    
    public Tournament confirmTournamentDetails(Tournament tournament) {
        // TODO Auto-generated method stub
        return tournament;
    }

    

}