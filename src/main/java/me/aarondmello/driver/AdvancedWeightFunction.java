package me.aarondmello.driver;

import me.aarondmello.datatypes.Player;

import java.util.*;

public class AdvancedWeightFunction implements WeightFunction {
    private static final int WEIGHT_OF_SIT_OUT = 10_000_000;
    private static final int NORMAL_POINT_FACTOR = 10_000;
    private static final int NORMAL_COST_OF_SAME_ORG = 1;

    @Override
    public Constraint getWeightConstraint(int bestWeight) {
        return new AdvancedWeightConstraint(bestWeight);
    }

    @Override
    public int calculateWeight(int opponentIndex, Player player, int roundIndex, List<Player> players) {
        if(opponentIndex == -1)
            return (player.hasSatOut()? 2* WEIGHT_OF_SIT_OUT : WEIGHT_OF_SIT_OUT) +
                    ((roundIndex == 0)? player.getScore() * player.getScore() * 5 * NORMAL_POINT_FACTOR : 0);

        Player opponent = players.get(opponentIndex);
        if (roundIndex == 0)
            return (player.getScore() - opponent.getScore()) *
                    (player.getScore() - opponent.getScore()) * NORMAL_POINT_FACTOR +
                    (player.getOrganization().equals(opponent.getOrganization())? NORMAL_COST_OF_SAME_ORG:0);
        return 0;
    }

    class AdvancedWeightConstraint implements Constraint{
        int bestWeight;
        int bestWeightForFirstRound = Integer.MIN_VALUE;
        int bestWeightForOtherRounds = Integer.MIN_VALUE;
        AdvancedWeightConstraint(int bestWeight){
            this.bestWeight = bestWeight;
        }


        public int getBestWeightPossible(PairingSystem.State state, VariableIndex index, VariableAssignment assignment, List<Player> players) {
            int weightForFirstRound = getWeightForFirstRound(state, index, assignment, players);
            int weightForOtherRounds = getWeightForOtherRounds(state, index, assignment, players);
            return weightForOtherRounds + weightForFirstRound;
        }

        private int getWeightForOtherRounds(PairingSystem.State state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            if(bestWeightForOtherRounds != Integer.MIN_VALUE){
                if(variableIndex.round() == 0 || assignment.opponentIndex() != -1) return bestWeightForOtherRounds;
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(variableIndex.round())) + (state.getVar(variableIndex).isSingleton()? 0:1);
                return bestWeightForOtherRounds + (numPlayersNotSittingOut % 2) * WEIGHT_OF_SIT_OUT;
            }

            int weightForOtherRounds = (variableIndex.round() == 0) ? 0 : assignment.weight();
            for(int r = 1; r < state.variables[0].length; r++){
                for (int i = 0; i < players.size(); i++) {
                    VariableIndex index = new VariableIndex(i, r);
                    if(index.equals(variableIndex))
                        continue;
                    if(state.getVar(index).isSingleton() && state.getVar(index).getValue() == -1)
                        weightForOtherRounds += state.getWeightOf(index);
                }
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(r)) + ((r == variableIndex.round() && assignment.opponentIndex() == -1)? 1:0);
                if(numPlayersNotSittingOut % 2 == 1) //If an odd number of players are not marked as sitting out in a round, we will find one more
                    weightForOtherRounds += WEIGHT_OF_SIT_OUT;
            }

            if(variableIndex.round() == 0)
                bestWeightForOtherRounds = weightForOtherRounds;
            return weightForOtherRounds;
        }

        private void addToMap(TreeMap<Integer, HashMap<String, Integer>> a, Player p, int score){
            HashMap<String, Integer> freqMap = a.get(score);
            if(freqMap == null)
                freqMap = new HashMap<>();
            Integer x = freqMap.get("TOTAL");
            if(x == null)
                x = 0;
            x++;
            freqMap.put("TOTAL", x);
            x = freqMap.get(p.getOrganization());
            if(x == null)
                x = 0;
            x++;
            freqMap.put(p.getOrganization(), x);

            a.put(score, freqMap);
        }

        private int getWeightForFirstRound(PairingSystem.State state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            if(bestWeightForFirstRound != Integer.MIN_VALUE && variableIndex.round() != 0)
                return bestWeightForFirstRound;

            int weightForFirstRound = 0;
            HashSet<Integer> firstRoundPlayersPaired = new HashSet<>();
            TreeMap<Integer, HashMap<String, Integer>> scoreToFrequencyTable = new TreeMap<>();
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
                if(state.getVar(index).isSingleton()){
                    weightForFirstRound += state.getWeightOf(index);
                    if (state.getVar(index).getValue() != -1) {
                        weightForFirstRound += calculateWeight(i, players.get(state.getVar(index).getValue()), 0, players);
                        firstRoundPlayersPaired.add(state.getVar(index).getValue());
                        firstRoundPlayersPaired.add(i);
                    }
                }
                else
                    addToMap(scoreToFrequencyTable, players.get(i), players.get(i).getScore());
            }

            Iterator<Integer> iterator = scoreToFrequencyTable.navigableKeySet().descendingIterator();
            int a = -1;
            HashMap<String, Integer> aFreqTable = null;
            while (a != -1 || iterator.hasNext()){
                if(a == -1){
                    a = iterator.next();
                    aFreqTable = scoreToFrequencyTable.get(a);
                }

                if(aFreqTable.get("TOTAL") % 2 == 0){
                    String mostCommonOrg = getMostCommonOrg(aFreqTable, null);
                    int numPlayersSameOrgRemaining = aFreqTable.get(mostCommonOrg) * 2 - aFreqTable.get("TOTAL");
                    weightForFirstRound += Math.max(numPlayersSameOrgRemaining, 0) * NORMAL_COST_OF_SAME_ORG;
                    a = -1;
                }
                else if(iterator.hasNext()){
                    String mostCommonOrg = getMostCommonOrg(aFreqTable, null);
                    int numPlayersSameOrgRemaining = aFreqTable.get(mostCommonOrg) * 2 - aFreqTable.get("TOTAL");
                    weightForFirstRound += Math.max(numPlayersSameOrgRemaining - 1, 0) * NORMAL_COST_OF_SAME_ORG;

                    int nextScore = iterator.next();
                    weightForFirstRound += (a-nextScore) * (a-nextScore) * 2 * NORMAL_POINT_FACTOR; //Add the weight of pairing two players with neighbouring scores
                    a = nextScore;
                    aFreqTable = scoreToFrequencyTable.get(nextScore);

                    String mostCommonOrgForNextScore = getMostCommonOrg(aFreqTable, (numPlayersSameOrgRemaining > 0)? mostCommonOrg:null);
                    if(mostCommonOrgForNextScore == null){
                        mostCommonOrgForNextScore = getMostCommonOrg(aFreqTable, null);
                        weightForFirstRound += 2;
                    }

                    aFreqTable.put(mostCommonOrgForNextScore, aFreqTable.get(mostCommonOrgForNextScore) - 1);
                    aFreqTable.put("TOTAL", aFreqTable.get("TOTAL") - 1);
                }
                else{
                    String mostCommonOrg = getMostCommonOrg(aFreqTable, null);
                    int numPlayersSameOrgRemaining = aFreqTable.get(mostCommonOrg) * 2 - aFreqTable.get("TOTAL");
                    weightForFirstRound += Math.max(numPlayersSameOrgRemaining - 1, 0) * NORMAL_COST_OF_SAME_ORG;
                    weightForFirstRound += WEIGHT_OF_SIT_OUT + 5 * a * a * NORMAL_POINT_FACTOR;//Sit out the worst player if there are an odd number
                    a = -1;
                }
            }

            if(variableIndex.round() != 0)
                bestWeightForFirstRound = weightForFirstRound;
            return weightForFirstRound;
        }

        private String getMostCommonOrg(HashMap<String, Integer> stringIntegerHashMap, String ignoreOrg) {
            int max = 0;
            String mostCommon = null;
            for (String org : stringIntegerHashMap.keySet()) {
                if(org.equals("TOTAL") || (ignoreOrg != null && ignoreOrg.equals(org))) continue;
                
                if(stringIntegerHashMap.get(org) > max){
                    max = stringIntegerHashMap.get(org);
                    mostCommon = org;
                }
            }
            return mostCommon;
        }

        @Override
        public Iterable<VariableIndex> applyTo(PairingSystem.State state, List<Player> players) {
            List<VariableIndex> modified = new LinkedList<>();
            for (int i = 0; i < state.variables.length; i++) {
                for (int j = 0; j < state.variables[i].length; j++) {
                    PairingSystem.Variable v = state.getVar(i, j);
                    VariableIndex coordinate = new VariableIndex(i, j);
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
