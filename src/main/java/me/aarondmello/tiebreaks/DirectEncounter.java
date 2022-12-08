package me.aarondmello.tiebreaks;

import me.aarondmello.datatypes.Player;

import java.util.*;

public class DirectEncounter implements Tiebreak {
    @Override
    public String name() {
        return TiebreakType.DirectEncounter.name();
    }

    @Override
    public TiebreakType type(){return TiebreakType.DirectEncounter;}

    public void computeSameScore(List<Player> players){
        if(players.size() == 1){
            players.get(0).setTiebreak(TiebreakType.DirectEncounter, 0);
            return;
        }

        Map<Player, Integer> playerToTiebreak = new HashMap<>();
        int[] maxRange = new int[]{0, 0};
        Player maxPlayer = players.get(0);
        boolean isOverlap = false;
        //Get a range
        for(Player p : players){
            int[] scoreRange = new int[]{0, 0};
            for(Player q : players){
                if(p.equals(q))
                    continue;

                int pointsEarned = p.getScoreAgainst(q);
                if(pointsEarned == -1){
                    scoreRange[1] += 2;
                }
                else{
                    scoreRange[0] += pointsEarned;
                    scoreRange[1] += pointsEarned;
                }
            }

            playerToTiebreak.put(p, scoreRange[1]);
            if(scoreRange[1] > maxRange[1]){
                maxRange = scoreRange;
                maxPlayer = p;
            }

        }

        for(Player p : players){
            if(p.equals(maxPlayer))
                continue;
            int maxScore = playerToTiebreak.get(p);
            if(maxScore >= maxRange[0]){
                isOverlap = true;
                break;
            }
        }

        if(isOverlap){
            for(Player p : players){
                p.setTiebreak(TiebreakType.DirectEncounter, 0);
            }
        }

        else{
            maxPlayer.setTiebreak(TiebreakType.DirectEncounter, players.size());
            players.remove(maxPlayer);
            computeSameScore(players);
        }
    }

    @Override
    public void computeTiebreak(List<Player> players, Comparator<Player> playerComparator) {
        players.sort(playerComparator);
        ArrayList<Player> ofSameScore = new ArrayList<>();

        for(Player p : players){
            if(ofSameScore.size() == 0 || playerComparator.compare(p, ofSameScore.get(0)) == 0)
                ofSameScore.add(p);
            else{
                computeSameScore(ofSameScore);
                ofSameScore.clear();
            }
        }

        computeSameScore(ofSameScore);
    }
}
