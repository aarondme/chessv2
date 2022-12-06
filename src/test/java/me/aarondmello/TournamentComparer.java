package me.aarondmello;

import me.aarondmello.datatypes.*;
import me.aarondmello.tiebreaks.Tiebreak;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TournamentComparer {
    public static final String areEqual = "areEqual";
    String result;
    public String compare(Tournament a, Tournament b){
        result = areEqual;
        compareBasic(a, b);
        compareDivisions(a, b);
        return result;
    }

    private void compareDivisions(Tournament a, Tournament b) {
        if(a.getDivisions().size() != b.getDivisions().size()){
            addToResult("Not the same amount of divisions");
            return;
        }

        for(Division d : a.getDivisions()){
            Division db = b.getDivisionWithName(d.getName(), false);
            if(db == null){
                addToResult("missing division " + d.getName());
                continue;
            }
            Tiebreak[] dtb = d.getTiebreaks();
            Tiebreak[] dbtb = db.getTiebreaks();
            if(dtb.length != dbtb.length){
                addToResult("division " + d.getName() + " inconsistent tiebreaks");
                continue;
            }
            for(int i = 0; i < dtb.length; i++){
                if(!dtb[i].name().equals(dbtb[i].name())){
                    addToResult("division " + d.getName() + " inconsistent tiebreaks");
                    break;
                }
            }


            comparePlayers(d, db);
        }
    }

    private void comparePlayers(Division d, Division db) {
        if(d.getPlayers().size() != db.getPlayers().size()){
            addToResult("players in division" + d.getName() + "mismatch");
            return;
        }

        for(Player p : d.getPlayers()){
            Player pq = db.getPlayerById(p.getID());
            if(pq == null){
                addToResult("player " + p.getID() + "not found in division " +  d.getName());
                continue;
            }

            if (!p.getName().equals(pq.getName()) || !p.getOrganization().equals(pq.getOrganization())) {
                addToResult("player data not matching");
                continue;
            }

            compareGames(p, pq);
            compareTiebreaks(p,pq);
        }
    }

    private void compareGames(Player p, Player pq) {
        List<PlayerGameSummary> g1 = p.getPlayerGameSummaries();
        List<PlayerGameSummary> g2 = pq.getPlayerGameSummaries();
        if(g1.size() != g2.size()){
            addToResult("Number of games on player " + p.getID() + " not matching");
            return;
        }
        for(int i = 0; i < g1.size(); i++){
            PlayerGameSummary a = g1.get(i);
            PlayerGameSummary b = g2.get(i);
            if(a.getPointsEarned() == b.getPointsEarned()
            && a.getOpponent().getID() == b.getOpponent().getID()
            && a.getColour() == b.getColour())
                continue;
            addToResult("GameSummary " + i + "on player " + p.getID() + " not matching");
            return;
        }
    }

    private void compareTiebreaks(Player p, Player pq) {
        Map<String, Integer> tb1 = p.getTiebreaks();
        Map<String, Integer> tb2 = pq.getTiebreaks();
        if (!tb1.keySet().equals(tb2.keySet())){
            addToResult("Tiebreaks on player " + p.getID() + " not matching");
        }
    }


    private void compareBasic(Tournament a, Tournament b) {
        if(!a.getName().equals(b.getName()))
            addToResult("tournament names");
        if(a.getRoundNumber() != b.getRoundNumber())
            addToResult("tournament round number");
        if(a.getTotalRounds() != b.getTotalRounds())
            addToResult("tournament total rounds");
    }

    private void addToResult(String error) {
        if(result.equals(areEqual))
            result = error;
        else
            result += "," + error;
    }
}
