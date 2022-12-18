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


        public int getBestWeightPossible(PairingSystem.State state, int[] coordinate, int opponentIndex, int weight, List<Player> players) {
            int weightForFirstRound = getWeightForFirstRound(state, coordinate, opponentIndex, weight, players);
            int weightForOtherRounds = getWeightForOtherRounds(state, coordinate, opponentIndex, weight, players);
            return weightForOtherRounds + weightForFirstRound;
        }

        private int getWeightForOtherRounds(PairingSystem.State state, int[] coordinate, int opponentIndex, int weight, List<Player> players) {
            if(bestWeightForOtherRounds != Integer.MIN_VALUE && (coordinate[1] == 0 || opponentIndex != -1))
                return bestWeightForOtherRounds;

            if(bestWeightForOtherRounds != Integer.MIN_VALUE){
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(coordinate[1])) + (state.getVar(coordinate).isSingleton()? 0:1);
                if(numPlayersNotSittingOut % 2 == 1)
                    return bestWeightForOtherRounds + WEIGHT_OF_SIT_OUT;
            }

            int weightForOtherRounds = (coordinate[1] == 0) ? 0 : weight;
            for(int r = 1; r < state.variables[0].length; r++){
                for (int i = 0; i < players.size(); i++) {
                    if(i == coordinate[0] && r == coordinate[1])
                        continue;
                    if(state.getVar(i, r).isSingleton() && state.getVar(i, r).getValue() == -1)
                        weightForOtherRounds += state.getWeightOf(i, r);
                }
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(r)) + ((r == coordinate[1] && opponentIndex == -1)? 1:0);
                if(numPlayersNotSittingOut % 2 == 1) //If an odd number of players are not marked as sitting out in a round, we will find one more
                    weightForOtherRounds += WEIGHT_OF_SIT_OUT;
            }

            if(coordinate[1] == 0)
                bestWeightForOtherRounds = weightForOtherRounds;
            return weightForOtherRounds;
        }

        private int getWeightForFirstRound(PairingSystem.State state, int[] coordinate, int opponentIndex, int weight, List<Player> players) {
            if(bestWeightForFirstRound != Integer.MIN_VALUE && coordinate[1] != 0)
                return bestWeightForFirstRound;

            int weightForFirstRound = 0;
            HashSet<Integer> firstRoundPlayersPaired = new HashSet<>();
            TreeMap<Integer, Integer> scoreToFreq = new TreeMap<>();
            if(coordinate[1] == 0 && opponentIndex != -1){
                firstRoundPlayersPaired.add(coordinate[0]);
                firstRoundPlayersPaired.add(opponentIndex);
                weightForFirstRound += calculateWeight(coordinate[0], players.get(opponentIndex), 0, players) + weight;
            }
            for (int i = 0; i < players.size(); i++) {
                if(firstRoundPlayersPaired.contains(i)) continue;

                if(state.getVar(i, 0).isSingleton()){
                    weightForFirstRound += state.getWeightOf(i, 0);
                    if (state.getVar(i, 0).getValue() != -1) {
                        weightForFirstRound += calculateWeight(i, players.get(state.getVar(i, 0).getValue()), 0, players);
                        firstRoundPlayersPaired.add(state.getVar(i, 0).getValue());
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

            if(coordinate[1] != 0)
                bestWeightForFirstRound = weightForFirstRound;
            return weightForFirstRound;
        }

        @Override
        public Iterable<int[]> applyTo(PairingSystem.State state, List<Player> players) {
            List<int[]> modified = new LinkedList<>();
            for (int i = 0; i < state.variables.length; i++) {
                for (int j = 0; j < state.variables[i].length; j++) {
                    PairingSystem.Variable v = state.getVar(i, j);
                    int[] coordinate = new int[]{i, j};
                    if(v.getDomain().removeIf(a -> getBestWeightPossible(state, coordinate, a.opponentIndex(), a.weight(), players) >= bestWeight)){
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
