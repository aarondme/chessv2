package me.aarondmello;

import me.aarondmello.datatypes.Division;
import me.aarondmello.datatypes.Player;
import me.aarondmello.datatypes.Round;
import me.aarondmello.datatypes.Tournament;
import me.aarondmello.tiebreaks.TiebreakType;

public class TournamentBuilder {
    Tournament t;
    TournamentBuilder(){
        t = new Tournament();
    }
    public static TournamentBuilder createTournament(){
        return new TournamentBuilder();
    }

    public TournamentBuilder withName(String s){
        t.setName(s);
        return this;
    }

    public TournamentBuilder withNRounds(int n){
        t.setTotalRounds(n);
        return this;
    }

    public TournamentBuilder withPlayer(String division, Player player){
        t.addPlayer(division, player);
        return this;
    }

    public TournamentBuilder withDivisionTiebreaks(String division, TiebreakType[] tiebreaks){
        Division d = t.getDivisionWithName(division, true);
        d.setTiebreaks(tiebreaks);
        return this;
    }
    public Tournament execute(){
        t.initialize(false);
        return t;
    }

    public TournamentBuilder withRound(String division, Round r) {
        Division d = t.getDivisionWithName(division, true);
        t.setRoundNumber(t.getRoundNumber() + 1);
        d.setCurrentRound(r);
        d.confirmRoundResults();
        return this;
    }
}
