package me.aarondmello.driver;

import me.aarondmello.datatypes.Player;

import java.util.*;

public class AdvancedWeightFunction implements WeightFunction {
    private static final int WEIGHT_OF_SIT_OUT = 100_000_000;
    private static final int NORMAL_POINT_FACTOR = 1_000_000;
    private static final int NORMAL_COST_OF_SAME_ORG = 1;
    private static final int WEAK_POINT_FACTOR = 100;
    private static final int WEAK_COST_OF_SAME_ORG = 10_000;
    private final int scoreCutoff;

    public AdvancedWeightFunction(List<Player> players, int roundsRemaining, int cutoff){
        scoreCutoff = getScoreCutoff(players, roundsRemaining, cutoff);
    }

    public AdvancedWeightFunction(List<Player> players, int roundsRemaining){
        scoreCutoff = getScoreCutoff(players, roundsRemaining, 2);
    }

    private int getScoreCutoff(List<Player> players, int roundsRemaining, int cutoff){
        ArrayList<Integer> scores = new ArrayList<>();
        for (Player p : players) {
            int score = p.getScore();
            scores.add(score);
        }
        scores.sort(Comparator.reverseOrder());
        return scores.get(cutoff-1) - 2 * roundsRemaining;
    }
    @Override
    public Constraint getWeightConstraint(int bestWeight) {
        return new AdvancedWeightConstraint(bestWeight);
    }

    @Override
    public int calculateWeight(int opponentIndex, Player player, int roundIndex, List<Player> players) {
        int pointFactor = (player.getScore() >= scoreCutoff)? NORMAL_POINT_FACTOR:WEAK_POINT_FACTOR;
        int orgFactor = (player.getScore() >= scoreCutoff)? NORMAL_COST_OF_SAME_ORG:WEAK_COST_OF_SAME_ORG;

        if(opponentIndex == -1){
            return WEIGHT_OF_SIT_OUT + (player.hasSatOut()? WEIGHT_OF_SIT_OUT:0)
                    + ((roundIndex==0)? player.getScore() * player.getScore() * 5 * pointFactor:0);
        }


        Player opponent = players.get(opponentIndex);
        if (roundIndex == 0){
            return (player.getScore() - opponent.getScore()) *
                    (player.getScore() - opponent.getScore()) * pointFactor +
                    (player.getOrganization().equals(opponent.getOrganization())? orgFactor:0);
        }

        return 0;
    }

    class AdvancedWeightConstraint implements Constraint{
        int bestWeight;
        int bestWeightForFirstRound = Integer.MIN_VALUE;
        int bestWeightForOtherRounds = Integer.MIN_VALUE;

        AdvancedWeightConstraint(int bestWeight){
            this.bestWeight = bestWeight;
        }

        public int getBestWeightPossible(VariableState state, VariableIndex index, VariableAssignment assignment, List<Player> players) {
            int weightForFirstRound = getWeightForFirstRound(state, index, assignment, players);
            int weightForOtherRounds = getWeightForOtherRounds(state, index, assignment, players);
            return weightForOtherRounds + weightForFirstRound;
        }

        private int getWeightForOtherRounds(VariableState state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            if(bestWeightForOtherRounds != Integer.MIN_VALUE){
                if(variableIndex.round() == 0 || assignment.opponentIndex() != -1) return bestWeightForOtherRounds;
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(variableIndex.round())) + ((state.getVar(variableIndex).size() == 1)? 0:1);
                return bestWeightForOtherRounds + (numPlayersNotSittingOut & 1) * WEIGHT_OF_SIT_OUT;
            }

            int weightForOtherRounds = (variableIndex.round() == 0) ? 0 : assignment.weight();
            for(int r = 1; r < state.roundsRemaining; r++){
                for (int i = 0; i < players.size(); i++) {
                    VariableIndex index = new VariableIndex(i, r);
                    if(index.equals(variableIndex))
                        continue;
                    LinkedList<VariableAssignment> domain = state.getVar(index);

                    if(domain.size() == 1 && domain.get(0).opponentIndex() == -1)
                        weightForOtherRounds += state.getWeightOf(index);
                }
                int numPlayersNotSittingOut = players.size() - state.numSitOuts(r) + ((r == variableIndex.round() && assignment.opponentIndex() == -1)? 1:0);
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
            int mostCommonWeakOrgFreq = 0;
            int mostCommonStrongOrgFreq = 0;
            int numStrongPlayers = 0;
            int numWeakPlayers = 0;
            HashSet<Integer> firstRoundPlayersPaired = new HashSet<>();
            TreeMap<Integer, Integer> scoreToCount = new TreeMap<>();
            HashMap<String, Integer> strongOrgToCount = new HashMap<>();
            HashMap<String, Integer> weakOrgToCount = new HashMap<>();
            if(variableIndex.round() == 0){
                firstRoundPlayersPaired.add(variableIndex.player());
                weightForFirstRound += assignment.weight();
                if(assignment.opponentIndex() != -1){
                    firstRoundPlayersPaired.add(assignment.opponentIndex());
                    weightForFirstRound += calculateWeight(variableIndex.player(), players.get(assignment.opponentIndex()), 0, players);
                }
            }
            for (int i = 0; i < players.size(); i++) {
                if(firstRoundPlayersPaired.contains(i)) continue;
                VariableIndex index = new VariableIndex(i, 0);
                LinkedList<VariableAssignment> domain = state.getVar(index);
                Player playerI = players.get(i);

                if(domain.size() == 1){
                    weightForFirstRound += state.getWeightOf(index);
                    int opponentIndex = domain.get(0).opponentIndex();
                    if (opponentIndex != -1) {
                        weightForFirstRound += calculateWeight(i, players.get(opponentIndex), 0, players);
                        firstRoundPlayersPaired.add(opponentIndex);
                        firstRoundPlayersPaired.add(i);
                    }
                    continue;
                }

                if(playerI.getScore() >= scoreCutoff){
                    int count =  strongOrgToCount.getOrDefault(playerI.getOrganization(), 0) + 1;
                    strongOrgToCount.put(playerI.getOrganization(), count);
                    numStrongPlayers++;
                    mostCommonStrongOrgFreq = Math.max(mostCommonStrongOrgFreq, count);
                }

                else{
                    int count = weakOrgToCount.getOrDefault(playerI.getOrganization(), 0) + 1;
                    weakOrgToCount.put(playerI.getOrganization(), count);
                    numWeakPlayers++;
                    mostCommonWeakOrgFreq = Math.max(mostCommonWeakOrgFreq, count);
                }
                scoreToCount.put(playerI.getScore(), scoreToCount.getOrDefault(playerI.getScore(), 0) + 1);
            }

            boolean wasPreviousOdd = false;
            int prevScore = -1;
            for (int score :
                    scoreToCount.descendingKeySet()) {
                int freq = scoreToCount.get(score);
                if(wasPreviousOdd){
                    weightForFirstRound += (prevScore - score) * (prevScore - score) *
                            (
                                    ((prevScore >= scoreCutoff)? NORMAL_POINT_FACTOR:WEAK_POINT_FACTOR) +
                                            ((score>=scoreCutoff)? NORMAL_POINT_FACTOR:WEAK_POINT_FACTOR)
                            );
                    freq--;
                }

                if((freq & 1) == 1){
                    wasPreviousOdd = true;
                    prevScore = score;
                }
                else{
                    wasPreviousOdd = false;
                }
            }

            if(wasPreviousOdd)
                weightForFirstRound += WEIGHT_OF_SIT_OUT + prevScore * prevScore * 5 * ((prevScore >= scoreCutoff)? NORMAL_POINT_FACTOR:WEAK_POINT_FACTOR);

            weightForFirstRound += NORMAL_COST_OF_SAME_ORG * Math.max((2 * mostCommonStrongOrgFreq - numStrongPlayers - (numStrongPlayers & 1)), 0);
            weightForFirstRound += WEAK_COST_OF_SAME_ORG * Math.max(2 * mostCommonWeakOrgFreq - numWeakPlayers - (numStrongPlayers & 1) -
                    ((numWeakPlayers - (numStrongPlayers & 1)) & 1),0);


            if(variableIndex.round() != 0)
                bestWeightForFirstRound = weightForFirstRound;
            return weightForFirstRound;
        }

        @Override
        public Iterable<VariableIndex> applyTo(VariableState state, List<Player> players) {
            List<VariableIndex> modified = new LinkedList<>();
            for (int i = 0; i < players.size(); i++) {
                for (int j = 0; j < state.roundsRemaining; j++) {
                    LinkedList<VariableAssignment> v = state.getVar(i, j);
                    VariableIndex coordinate = new VariableIndex(i, j);
                    if(v.removeIf(a -> getBestWeightPossible(state, coordinate, a, players) >= bestWeight)){
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
