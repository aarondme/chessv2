package me.aarondmello.tiebreaks;

import java.util.ArrayList;

import me.aarondmello.datatypes.Player;

public class TiebreakInitializer {
    public void initialize(Player p){
        //TODO add enum for different settings
        ArrayList<Tiebreak> tiebreaks = p.getTiebreaks();
        initializeDefaults(tiebreaks);
    }

    private void initializeDefaults(ArrayList<Tiebreak> tiebreaks){
        tiebreaks.clear();
        tiebreaks.add(new BuchholzCutOne());
        tiebreaks.add(new Buchholz());
        tiebreaks.add(new SonnebornBerger());
        tiebreaks.add(new ProgressiveScores());
        tiebreaks.add(new DirectEncounter());
        tiebreaks.add(new WinCount());
        tiebreaks.add(new WinCountAsBlack());
    }
}
