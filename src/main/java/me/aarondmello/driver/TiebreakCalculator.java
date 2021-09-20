package me.aarondmello.driver;

import java.util.*;
import java.util.function.BiConsumer;

public class TiebreakCalculator {
    private static HashMap<Integer, BiConsumer<LinkedList<GameResult>, Tiebreaks>> updateTiebreakMethods = new HashMap<>(){{
        put(TiebreakIndex.BUCHOLZ, (LinkedList<GameResult> o, Tiebreaks t) -> updateBuchholz(o, t));
        put(TiebreakIndex.BUCHOLZ_CUT_ONE, (LinkedList<GameResult> o, Tiebreaks t) -> updateBuchholzCutOne(o, t));
        put(TiebreakIndex.SONNEBORN_BERGER, (LinkedList<GameResult> o, Tiebreaks t) -> updateSonnebornBerger(o, t));
        put(TiebreakIndex.PROGRESSIVE_SCORES, (LinkedList<GameResult> o, Tiebreaks t) -> updateProgressiveScores(o, t));
        put(TiebreakIndex.WIN_COUNT, (LinkedList<GameResult> o, Tiebreaks t) -> updateWinCount(o, t));
        put(TiebreakIndex.WIN_COUNT_AS_BLACK, (LinkedList<GameResult> o, Tiebreaks t) -> updateWinCountAsBlack(o, t));
    }};
    public static void updateTiebreaks(LinkedList<GameResult> gameresults, int[] tiebreaksToUpdate, Tiebreaks tiebreaks){
        for(int i : tiebreaksToUpdate){
            BiConsumer<LinkedList<GameResult>, Tiebreaks> method = updateTiebreakMethods.get(i);
            if(method != null)
                method.accept(gameresults, tiebreaks); 
        }
    }
    private static void updateBuchholzCutOne(LinkedList<GameResult> gameResults, Tiebreaks t){
        int minScore = Integer.MAX_VALUE;
        int buchholzCutOne = 0;
        for(GameResult m : gameResults){
            int opponentScore = m.getOpponent().getScore();
            if(opponentScore < minScore){
                if(minScore == Integer.MAX_VALUE){
                    minScore = opponentScore;
                }
                else{
                    buchholzCutOne += minScore;
                    minScore = opponentScore;
                }
            }
            else
                buchholzCutOne += opponentScore;
        }
        t.setBuchholzCutOne(buchholzCutOne); 
    }
    private static void updateBuchholz(LinkedList<GameResult> gameResults, Tiebreaks t){
        int buchholz = 0;
        for(GameResult m : gameResults){
            int opponentScore = m.getOpponent().getScore();
            buchholz += opponentScore;
        }
        t.setBuchholz(buchholz);
    }
    private static void updateSonnebornBerger(LinkedList<GameResult> gameResults, Tiebreaks t){
        int sonnebornBerger = 0;
        for(GameResult m : gameResults){
            int opponentScore = m.getOpponent().getScore();
            sonnebornBerger += opponentScore * m.getPointsEarned();
        }
        t.setSonnebornBerger(sonnebornBerger); 
    }
    private static void updateProgressiveScores(LinkedList<GameResult> gameResults, Tiebreaks t){
        int progressiveScores = 0;
        int currentScore = 0;
        for(GameResult m : gameResults){
            int gameScore = m.getPointsEarned();
            currentScore += gameScore;
            progressiveScores += currentScore;
        }
        t.setProgressiveScores(progressiveScores); 
    }
    private static void updateWinCount(LinkedList<GameResult> gameResults, Tiebreaks t){
        int winCount = 0;
        for(GameResult m : gameResults){
            if(m.getPointsEarned() == 2) winCount++;
        }
        t.setWinCount(winCount); 
    }
    private static void updateWinCountAsBlack(LinkedList<GameResult> gameResults, Tiebreaks t){
        int winCountAsBlack = 0;
        for(GameResult m : gameResults){
            if(m.getPointsEarned() == 2 && m.getColour() == Colour.BLACK) 
                winCountAsBlack++;
        }
        t.setWinCountAsBlack(winCountAsBlack);
    }
}
