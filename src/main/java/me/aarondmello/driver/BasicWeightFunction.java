package me.aarondmello.driver;

import me.aarondmello.datatypes.Player;

import java.util.*;

public class BasicWeightFunction implements WeightFunction {
    private static final int WEIGHT_OF_SIT_OUT = 1_000_000;

    @Override
    public int calculateWeight(int opponentIndex, Player player, int roundIndex, List<Player> players) {
        if(opponentIndex == -1)
            return (player.hasSatOut()? 2* WEIGHT_OF_SIT_OUT : WEIGHT_OF_SIT_OUT) +
                    ((roundIndex == 0)? player.getScore() * player.getScore() * 5 : 0);

        Player opponent = players.get(opponentIndex);
        if (roundIndex == 0)
            return (player.getScore() - opponent.getScore()) *
                    (player.getScore() - opponent.getScore());
        return 0;
    }
    @Override
    public Constraint getWeightConstraint(int bestWeight) {
        return new BasicWeightConstraint(bestWeight);
    }
    class BasicWeightConstraint implements Constraint{
        int bestWeight;
        int bestWeightForFirstRound = Integer.MIN_VALUE;
        int bestWeightForOtherRounds = Integer.MIN_VALUE;
        BasicWeightConstraint(int bestWeight){
            this.bestWeight = bestWeight;
        }


        public int getBestWeightPossible(PairingSystem.State state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            int weightForFirstRound = getWeightForFirstRound(state, variableIndex, assignment, players);
            int weightForOtherRounds = getWeightForOtherRounds(state, variableIndex, assignment, players);
            return weightForOtherRounds + weightForFirstRound;
        }

        private int getWeightForOtherRounds(PairingSystem.State state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            if(bestWeightForOtherRounds != Integer.MIN_VALUE){
                if(variableIndex.round() == 0 || assignment.opponentIndex() != -1)  return bestWeightForOtherRounds;
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(variableIndex.round())) +
                        (state.getVar(variableIndex).isSingleton()? 0:1);
                return bestWeightForOtherRounds + (numPlayersNotSittingOut % 2) * WEIGHT_OF_SIT_OUT;
            }

            int weightForOtherRounds = (variableIndex.round() == 0) ? 0 : assignment.weight();
            for(int r = 1; r < state.variables[0].length; r++){
                for (int i = 0; i < players.size(); i++) {
                    VariableIndex indexToGet = new VariableIndex(i, r);
                    if(indexToGet.equals(variableIndex))
                        continue;
                    if(state.getVar(indexToGet).isSingleton() && state.getVar(indexToGet).getValue() == -1)
                        weightForOtherRounds += state.getWeightOf(indexToGet);
                }
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(r)) + ((r == variableIndex.round() && assignment.opponentIndex() == -1)? 1:0);
                if(numPlayersNotSittingOut % 2 == 1) //If an odd number of players are not marked as sitting out in a round, we will find one more
                    weightForOtherRounds += WEIGHT_OF_SIT_OUT;
            }

            if(variableIndex.round() == 0)
                bestWeightForOtherRounds = weightForOtherRounds;
            return weightForOtherRounds;
        }

        private int getWeightForFirstRound(PairingSystem.State state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            if(bestWeightForFirstRound != Integer.MIN_VALUE && variableIndex.round() != 0)
                return bestWeightForFirstRound;

            int weightForFirstRound = 0;
            HashSet<Integer> firstRoundPlayersPaired = new HashSet<>();
            TreeMap<Integer, Integer> scoreToFreq = new TreeMap<>();
            if(variableIndex.round() == 0 && assignment.opponentIndex() != -1){
                firstRoundPlayersPaired.add(variableIndex.player());
                firstRoundPlayersPaired.add(assignment.opponentIndex());
                weightForFirstRound += calculateWeight(variableIndex.player(), players.get(assignment.opponentIndex()), 0, players) + assignment.weight();
            }
            for (int i = 0; i < players.size(); i++) {
                if(firstRoundPlayersPaired.contains(i)) continue;
                VariableIndex indexToGet = new VariableIndex(i, 0);
                if(state.getVar(indexToGet).isSingleton()){
                    weightForFirstRound += state.getWeightOf(indexToGet);
                    if (state.getVar(indexToGet).getValue() != -1) {
                        weightForFirstRound += calculateWeight(i, players.get(state.getVar(indexToGet).getValue()), 0, players);
                        firstRoundPlayersPaired.add(state.getVar(indexToGet).getValue());
                    }
                }

                else {
                    Integer numOccur = scoreToFreq.get(players.get(i).getScore());
                    if(numOccur == null) numOccur = 0;
                    scoreToFreq.put(players.get(i).getScore(), numOccur + 1);
                }
            }

            Iterator<Integer> iterator = scoreToFreq.navigableKeySet().descendingIterator();
            int a = -1;
            int aFreq = -1;
            while (a != -1 || iterator.hasNext()){
                if(a == -1){
                    a = iterator.next();
                    aFreq = scoreToFreq.get(a);
                }

                if(aFreq % 2 == 0){
                    a = -1;
                }
                else if(iterator.hasNext()){
                    int b = iterator.next();
                    weightForFirstRound += (a-b) * (a-b) * 2; //Add the weight of pairing two players with neighbouring scores
                    a = b;
                    aFreq = scoreToFreq.get(b) - 1;
                }
                else{
                    weightForFirstRound += WEIGHT_OF_SIT_OUT + 5 * a * a;//Sit out the worst player if there are an odd number
                    a = -1;
                }
            }

            if(variableIndex.round() != 0)
                bestWeightForFirstRound = weightForFirstRound;
            return weightForFirstRound;
        }

        @Override
        public Iterable<VariableIndex> applyTo(PairingSystem.State state, List<Player> players) {
            List<VariableIndex> modified = new LinkedList<>();
            for (int i = 0; i < state.variables.length; i++) {
                for (int j = 0; j < state.variables[i].length; j++) {
                    VariableIndex coordinate = new VariableIndex(i, j);
                    PairingSystem.Variable v = state.getVar(coordinate);
                    if(v.getDomain().removeIf(a -> getBestWeightPossible(state, coordinate, a, players) >= bestWeight)){
                        if(v.isEmpty()) return null;
                        modified.add(coordinate);
                    }
                }
            }
            return modified;
        }

        @Override
        public String name() {
            return "w";
        }
    }
}
