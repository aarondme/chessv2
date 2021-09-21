package me.aarondmello.driver;

import java.util.*;
import java.util.function.BiConsumer;

import me.aarondmello.constants.Colour;
import me.aarondmello.constants.TiebreakIndex;
import me.aarondmello.datatypes.PlayerGameSummary;
import me.aarondmello.datatypes.Tiebreaks;

public class TiebreakCalculator {
    private static HashMap<Integer, BiConsumer<LinkedList<PlayerGameSummary>, Tiebreaks>> updateTiebreakMethods = new HashMap<>(){{
        put(TiebreakIndex.BUCHHOLZ, (LinkedList<PlayerGameSummary> o, Tiebreaks t) -> updateBuchholz(o, t));
        put(TiebreakIndex.BUCHHOLZ_CUT_ONE, (LinkedList<PlayerGameSummary> o, Tiebreaks t) -> updateBuchholzCutOne(o, t));
        put(TiebreakIndex.SONNEBORN_BERGER, (LinkedList<PlayerGameSummary> o, Tiebreaks t) -> updateSonnebornBerger(o, t));
        put(TiebreakIndex.PROGRESSIVE_SCORES, (LinkedList<PlayerGameSummary> o, Tiebreaks t) -> updateProgressiveScores(o, t));
        put(TiebreakIndex.WIN_COUNT, (LinkedList<PlayerGameSummary> o, Tiebreaks t) -> updateWinCount(o, t));
        put(TiebreakIndex.WIN_COUNT_AS_BLACK, (LinkedList<PlayerGameSummary> o, Tiebreaks t) -> updateWinCountAsBlack(o, t));
    }};
    public static void updateTiebreaks(LinkedList<PlayerGameSummary> PlayerGameSummarys, int[] tiebreaksToUpdate, Tiebreaks tiebreaks){
        for(int i : tiebreaksToUpdate){
            BiConsumer<LinkedList<PlayerGameSummary>, Tiebreaks> method = updateTiebreakMethods.get(i);
            if(method != null)
                method.accept(PlayerGameSummarys, tiebreaks); 
        }
    }
    private static void updateBuchholzCutOne(LinkedList<PlayerGameSummary> PlayerGameSummarys, Tiebreaks t){
        int minScore = Integer.MAX_VALUE;
        int buchholzCutOne = 0;
        for(PlayerGameSummary m : PlayerGameSummarys){
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
    private static void updateBuchholz(LinkedList<PlayerGameSummary> PlayerGameSummarys, Tiebreaks t){
        int buchholz = 0;
        for(PlayerGameSummary m : PlayerGameSummarys){
            int opponentScore = m.getOpponent().getScore();
            buchholz += opponentScore;
        }
        t.setBuchholz(buchholz);
    }
    private static void updateSonnebornBerger(LinkedList<PlayerGameSummary> PlayerGameSummarys, Tiebreaks t){
        int sonnebornBerger = 0;
        for(PlayerGameSummary m : PlayerGameSummarys){
            int opponentScore = m.getOpponent().getScore();
            sonnebornBerger += opponentScore * m.getPointsEarned();
        }
        t.setSonnebornBerger(sonnebornBerger); 
    }
    private static void updateProgressiveScores(LinkedList<PlayerGameSummary> PlayerGameSummarys, Tiebreaks t){
        int progressiveScores = 0;
        int currentScore = 0;
        for(PlayerGameSummary m : PlayerGameSummarys){
            int gameScore = m.getPointsEarned();
            currentScore += gameScore;
            progressiveScores += currentScore;
        }
        t.setProgressiveScores(progressiveScores); 
    }
    private static void updateWinCount(LinkedList<PlayerGameSummary> PlayerGameSummarys, Tiebreaks t){
        int winCount = 0;
        for(PlayerGameSummary m : PlayerGameSummarys){
            if(m.getPointsEarned() == 2) winCount++;
        }
        t.setWinCount(winCount); 
    }
    private static void updateWinCountAsBlack(LinkedList<PlayerGameSummary> PlayerGameSummarys, Tiebreaks t){
        int winCountAsBlack = 0;
        for(PlayerGameSummary m : PlayerGameSummarys){
            if(m.getPointsEarned() == 2 && m.getColour() == Colour.BLACK) 
                winCountAsBlack++;
        }
        t.setWinCountAsBlack(winCountAsBlack);
    }
}
