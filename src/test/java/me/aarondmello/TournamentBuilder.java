package me.aarondmello;

import me.aarondmello.datatypes.*;

import java.util.LinkedList;

public class TournamentBuilder {
    Tournament t;
    TournamentBuilder(){
        t = new Tournament("DefaultName", 5, true);
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

    public TournamentBuilder withRound(String division, LinkedList<Game> r) {
        Division d = t.getDivisionWithName(division, true);
        t.setRoundNumber(t.getRoundNumber() + 1);
        d.setCurrentRound(r);
        d.confirmRoundResults();
        return this;
    }

    public TournamentBuilder asFinals() {
        t.setRegionalTournament(false);
        return this;
    }
}
