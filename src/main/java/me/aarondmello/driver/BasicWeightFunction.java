package me.aarondmello.driver;

import me.aarondmello.datatypes.Player;

import java.util.*;
/**
 * Weight Function used for Finals tournaments:
 * A game is assigned weight according to the difference in scores.
 * Schools of players are ignored.
 */
public class BasicWeightFunction implements WeightFunction {
    private static final int WEIGHT_OF_SIT_OUT = 1_000_000;
    private static final int MULTIPLIER_OF_SIT_OUT = 5;

    @Override
    public int calculateWeight(int opponentIndex, Player player, int roundIndex, List<Player> players) {
        int playerScore = player.getScore();

        if(opponentIndex == VariableAssignment.SIT_OUT_INDEX)
            return WEIGHT_OF_SIT_OUT + (player.hasSatOut()? WEIGHT_OF_SIT_OUT :0) +
                    ((roundIndex == 0)? playerScore * playerScore * MULTIPLIER_OF_SIT_OUT : 0);

        Player opponent = players.get(opponentIndex);
        int opponentScore = opponent.getScore();
        if (roundIndex == 0)
            return (playerScore - opponentScore) *
                    (playerScore - opponentScore);
        return 0;
    }
    @Override
    public Constraint getWeightConstraint(int bestWeight) {
        return new BasicWeightConstraint(bestWeight);
    }
    class BasicWeightConstraint extends WeightConstraint{
        int bestWeight;
        int bestWeightForFirstRound = Integer.MIN_VALUE;
        int bestWeightForOtherRounds = Integer.MIN_VALUE;
        BasicWeightConstraint(int bestWeight){
            this.bestWeight = bestWeight;
        }

        @Override
        protected int weightToBeat() {
            return bestWeight;
        }

        @Override
        public int getBestWeightPossible(VariableState state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            int weightForFirstRound = getWeightForFirstRound(state, variableIndex, assignment, players);
            int weightForOtherRounds = getWeightForOtherRounds(state, variableIndex, assignment, players);
            return weightForOtherRounds + weightForFirstRound;
        }

        private int getWeightForOtherRounds(VariableState state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            if(bestWeightForOtherRounds != Integer.MIN_VALUE){
                if(variableIndex.round() == 0 || !assignment.isSittingOut())  return bestWeightForOtherRounds;
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(variableIndex.round())) +
                        ((state.getVar(variableIndex).size() == 1)? 0:1);
                return bestWeightForOtherRounds + (numPlayersNotSittingOut & 1) * WEIGHT_OF_SIT_OUT;
            }

            int weightForOtherRounds = (variableIndex.round() == 0) ? 0 : assignment.weight();
            for(int r = 1; r < state.roundsRemaining; r++){
                for (int i = 0; i < players.size(); i++) {
                    VariableIndex indexToGet = new VariableIndex(i, r);
                    if(indexToGet.equals(variableIndex))
                        continue;
                    if(state.getVar(indexToGet).size() == 1 && state.getVar(indexToGet).get(0).isSittingOut())
                        weightForOtherRounds += state.getWeightOf(indexToGet);
                }
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(r)) + ((r == variableIndex.round() && assignment.isSittingOut())? 1:0);
                weightForOtherRounds += (numPlayersNotSittingOut & 1) * WEIGHT_OF_SIT_OUT; //If an odd number of players are not marked as sitting out in a round, we will find one more
            }

            if(variableIndex.round() == 0)
                bestWeightForOtherRounds = weightForOtherRounds;
            return weightForOtherRounds;
        }

        private int getWeightForFirstRound(VariableState state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            if(bestWeightForFirstRound != Integer.MIN_VALUE && variableIndex.round() != 0)
                return bestWeightForFirstRound;

            int weightForFirstRound = 0;
            HashSet<Integer> firstRoundPlayersPaired = new HashSet<>();
            TreeMap<Integer, Integer> scoreToFreq = new TreeMap<>();
            if(variableIndex.round() == 0 && !assignment.isSittingOut()){
                firstRoundPlayersPaired.add(variableIndex.player());
                firstRoundPlayersPaired.add(assignment.opponentIndex());
                weightForFirstRound += calculateWeight(variableIndex.player(), players.get(assignment.opponentIndex()), 0, players) + assignment.weight();
            }
            for (int i = 0; i < players.size(); i++) {
                if(firstRoundPlayersPaired.contains(i)) continue;
                VariableIndex indexToGet = new VariableIndex(i, 0);
                List<VariableAssignment> domain = state.getVar(indexToGet);
                if(domain.size() == 1){
                    weightForFirstRound += state.getWeightOf(indexToGet);
                    int opponentIndex = domain.get(0).opponentIndex();
                    if (opponentIndex != VariableAssignment.SIT_OUT_INDEX) {
                        weightForFirstRound += calculateWeight(i, players.get(opponentIndex), 0, players);
                        firstRoundPlayersPaired.add(opponentIndex);
                    }
                }

                else {
                    int score = players.get(i).getScore();
                    Integer numOccur = scoreToFreq.getOrDefault(score, 0);
                    scoreToFreq.put(score, numOccur + 1);
                }
            }

            boolean wasPreviousOdd = false;
            int prevScore = -1;

            for (int score : scoreToFreq.descendingKeySet()) {
                int freq = scoreToFreq.get(score);
                if(wasPreviousOdd){
                    weightForFirstRound += (prevScore - score) * (prevScore - score) * 2;
                    freq--;
                }

                wasPreviousOdd = (freq & 1) == 1;
                prevScore = score;
            }


            if(wasPreviousOdd)
                weightForFirstRound += WEIGHT_OF_SIT_OUT + prevScore * prevScore * MULTIPLIER_OF_SIT_OUT;

            if(variableIndex.round() != 0)
                bestWeightForFirstRound = weightForFirstRound;
            return weightForFirstRound;
        }
    }
}
