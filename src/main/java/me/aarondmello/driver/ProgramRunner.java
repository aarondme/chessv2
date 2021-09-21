package me.aarondmello.driver;

import me.aarondmello.datatypes.Tournament;
import me.aarondmello.maininterfaces.DataExporter;
import me.aarondmello.maininterfaces.GUI;
import me.aarondmello.swinguserinterface.SwingUserInterface;

public class ProgramRunner{
    static GUI gui = new SwingUserInterface();
    static DataExporter dataExporter;
    public static void main(String[] args) {
        Tournament tournament = createTournament();
        runTournament(tournament);
        saveTournament(tournament);
    }

    public static Tournament createTournament(){
        Tournament tournament = null;
        while(!gui.wasCancelPressed() && tournament == null){
            if(gui.getIfNewTournament())
                tournament = gui.getNewTournament();
            else
                tournament = gui.getExistingTournament();
        }
        return tournament;
    }

    public static void runTournament(Tournament tournament){
        while(!gui.wasCancelPressed() && tournament.hasRoundsRemaining()){
            //tournament.runNextRound(gui); //TODO
            if(!gui.wasCancelPressed()) break;
            gui.displayResults(tournament);
        }
    }

    public static void saveTournament(Tournament tournament){
        if(tournament == null) return;
        if(gui.wasCancelPressed()){
            if(gui.getIfSavingResults()){
                //TODO
            }
        }
        else{
            gui.displayResults(tournament);
            //TODO: save results
        }
    }
}