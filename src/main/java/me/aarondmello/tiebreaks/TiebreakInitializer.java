package me.aarondmello.tiebreaks;

import java.util.ArrayList;

import me.aarondmello.datatypes.Player;

public class TiebreakInitializer {
    static TiebreakType[] defaultTiebreaks = {TiebreakType.BuchholzCutOne, TiebreakType.Buchholz,
                        TiebreakType.SonnebornBerger, TiebreakType.ProgressiveScores,
                        TiebreakType.DirectEncounter, TiebreakType.WinCount,
                        TiebreakType.WinCountAsBlack};
    TiebreakType[] tiebreaksTypes = defaultTiebreaks;


    public void initialize(Player p){
        ArrayList<Tiebreak> tiebreaks = new ArrayList<>();
        for(TiebreakType tiebreakType : tiebreaksTypes){
            switch (tiebreakType){
                case WinCountAsBlack -> tiebreaks.add(new WinCountAsBlack());
                case WinCount -> tiebreaks.add(new WinCount());
                case ProgressiveScores -> tiebreaks.add(new ProgressiveScores());
                case SonnebornBerger -> tiebreaks.add(new SonnebornBerger());
                case Buchholz -> tiebreaks.add(new Buchholz());
                case DirectEncounter -> tiebreaks.add(new DirectEncounter());
                case BuchholzCutOne -> tiebreaks.add(new BuchholzCutOne());
            }
        }
        p.setTiebreaks(tiebreaks);
    }

    public void setTiebreaks(TiebreakType[] tiebreaks){
        if(tiebreaks == null)
            tiebreaksTypes = defaultTiebreaks;
        else
            this.tiebreaksTypes = tiebreaks;
    }

    public String getTiebreakNames(){
        String out = "";
        for(TiebreakType t : tiebreaksTypes){
            out += "," + t.toString();
        }
        return out;
    }
}
