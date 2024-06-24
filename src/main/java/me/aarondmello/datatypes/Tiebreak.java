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
        return games.stream().mapToInt(m -> m.getOpponent().getScore()).sum();
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.Buchholz;
    }
}
class BuchholzCutOne implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        int score = games.stream().mapToInt(m -> m.getOpponent().getScore()).sum();
        OptionalInt leastScore = games.stream().mapToInt(m -> m.getOpponent().getScore()).min();
        if(leastScore.isEmpty())
            return 0;
        return score - leastScore.getAsInt();
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
        return games.stream().mapToInt(m -> m.getOpponent().getScore() * m.getPointsEarned()).sum();
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.SonnebornBerger;
    }
}

class WinCount implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        return (int) games.stream().filter(m -> m.getPointsEarned() == Game.WIN_POINTS).count();
    }

    @Override
    public TiebreakType type() {
        return TiebreakType.WinCount;
    }
}

class WinCountAsBlack implements ICalculateSimpleTiebreak {
    @Override
    public int calculateScore(LinkedList<PlayerGameSummary> games) {
        return (int) games.stream().filter(m -> m.getPointsEarned() == Game.WIN_POINTS && m.getColour() == Colour.BLACK).count();
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

    public void computeSameScore(Set<Player> players){
        if(players.size() == 1){
            players.iterator().next().setTiebreak(type(), 0);
            return;
        }

        int maxOptimisticScore = 0;
        Player tiebreakWinner = players.iterator().next();
        int maxActualScore = 0;

        for(Player p : players){
            LinkedList<PlayerGameSummary> playerGameSummaries = p.getPlayerGameSummaries();
            int gamesPlayedAgainstTiedOpponents = (int) playerGameSummaries.stream().filter(q -> players.contains(q.getOpponent()))
                    .count();
            int scoreAgainstTiedOpponents = playerGameSummaries.stream().filter(q -> players.contains(q.getOpponent()))
                    .mapToInt(PlayerGameSummary::getPointsEarned).sum();
            int maxScoreInUnplayedGames = (players.size() - gamesPlayedAgainstTiedOpponents) * Game.WIN_POINTS;
            int optimisticScoreAgainstTiedOpponents = scoreAgainstTiedOpponents + maxScoreInUnplayedGames;
            maxOptimisticScore = Math.max(maxOptimisticScore, optimisticScoreAgainstTiedOpponents);
            if(scoreAgainstTiedOpponents > maxActualScore){
                tiebreakWinner = p;
                maxActualScore = scoreAgainstTiedOpponents;
            }

        }

        if(maxOptimisticScore >= maxActualScore){
            for(Player p : players){
                p.setTiebreak(type(), 0);
            }
        }

        else{
            tiebreakWinner.setTiebreak(type(), players.size());
            players.remove(tiebreakWinner);
            computeSameScore(players);
        }
    }

    @Override
    public void computeTiebreak(List<Player> players, Comparator<Player> playerComparator) {
        players.sort(playerComparator);
        Set<Player> ofSameScore = new HashSet<>();
        for(Player p : players){
            if(ofSameScore.size() == 0 || playerComparator.compare(p, ofSameScore.iterator().next()) == 0){
                ofSameScore.add(p);
            }
            else{
                computeSameScore(ofSameScore);
                ofSameScore.clear();
            }
        }
        if(ofSameScore.size() > 0)
            computeSameScore(ofSameScore);
    }
}