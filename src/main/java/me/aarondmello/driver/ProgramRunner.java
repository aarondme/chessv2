package me.aarondmello.driver;

import me.aarondmello.swinguserinterface.SwingUserInterface;

import java.io.File;
import java.util.List;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.maininterfaces.*;

public class ProgramRunner implements TournamentManager{
    GUI gui;
    Persister persister = new Persister();
    ProgramRunner(GUI gui){
        this.gui = gui;
    }
    public static void main(String[] args) {
        GUI gui = new SwingUserInterface();
        gui.start(new ProgramRunner(gui));
    }

    @Override
    public Tournament createTournament() {
        File tournamentFolder = gui.getSaveLocation();
        if(tournamentFolder == null)
            return null;
        
        Tournament tournament = persister.scanFolder(tournamentFolder);
        if(persister.wasFileReadSuccessfully("config.txt"))
            tournament = gui.confirmTournamentDetails(tournament, persister.getFilesReadIterator());
        else
            tournament = gui.getTournamentDetails(tournament);
        
        return tournament;
    }

    @Override
    public void startNextRound(Tournament tournament) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void save(Tournament tournament) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public Tournament resumeTournament() {
        // TODO Auto-generated method stub
        return null;
    }


}