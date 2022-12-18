package me.aarondmello.datatypes;

import java.util.*;

public interface Tiebreak {
    String name();
    void computeTiebreak(List<Player> players, Comparator<Player> playerComparator);

    TiebreakType type();
}
class SimpleTiebreak implements Tiebreak{
    ICalculateSimpleTiebreak calculator;
    public SimpleTiebreak(ICalculateSimpleTiebreak iCalculateSimpleTiebreak){
        calculator = iCalculateSimpleTiebreak;
    }

    @Override
    public String name() {
        return calculator.type().name();
    }

    @Override
    public TiebreakType type(){return calculator.type();}

    @Override
    public void computeTiebreak(List<Player> players, Comparator<Player> playerComparator) {
        for(Player p : players)
            p.setTiebreak(calculator.type(), calculator.calculateScore(p.getPlayerGameSummaries()));
    }
}
interface ICalculateSimpleTiebreak {
    int calculateScore(LinkedList<PlayerGameSummary> games);

    TiebreakType type();
}

class Buchholz implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        for (PlayerGameSummary game : games) {
            int opponentScore = game.getOpponent().getScore();
            score += opponentScore;
        }
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.Buchholz;
    }
}
class BuchholzCutOne implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int minOpponentScore = 0;
        int score = 0;
        for (PlayerGameSummary game : games) {
            int opponentScore = game.getOpponent().getScore();
            score += opponentScore;
            if (opponentScore < minOpponentScore)
                minOpponentScore = opponentScore;
        }
        score -= minOpponentScore;
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.BuchholzCutOne;
    }
}

class ProgressiveScores implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        int runningTotal = 0;
        for (PlayerGameSummary game : games) {
            int gameScore = game.getPointsEarned();
            runningTotal += gameScore;
            score += runningTotal;
        }
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.ProgressiveScores;
    }
}

class SonnebornBerger implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        for (PlayerGameSummary game : games) {
            int opponentScore = game.getOpponent().getScore();
            score += opponentScore * game.getPointsEarned();
        }
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.SonnebornBerger;
    }
}

class WinCount implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        for (PlayerGameSummary game : games) {
            if (game.getPointsEarned() == 2)
                score++;
        }
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.WinCount;
    }
}

class WinCountAsBlack implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = 0;
        for (PlayerGameSummary game : games) {
            if (game.getPointsEarned() == 2 && game.getColour() == Colour.BLACK)
                score++;
        }
        return score;
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.WinCountAsBlack;
    }
}

class DirectEncounter implements Tiebreak {
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