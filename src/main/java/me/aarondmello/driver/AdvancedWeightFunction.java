package me.aarondmello.driver;

import me.aarondmello.datatypes.Player;

import java.util.*;

public class AdvancedWeightFunction implements WeightFunction {
    private static final int WEIGHT_OF_SIT_OUT = 10_000_000;
    private static final int NORMAL_POINT_FACTOR = 10_000;
    private static final int NORMAL_COST_OF_SAME_ORG = 1;

    @Override
    public Constraint getWeightConstraint(int bestWeight, int roundsRemaining, List<Player> players) {
        return new AdvancedWeightConstraint(bestWeight, roundsRemaining, players);
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
//        int scoreCutoff;

        AdvancedWeightConstraint(int bestWeight, int roundsRemaining, List<Player> players){
            this.bestWeight = bestWeight;
//            this.scoreCutoff = getScoreCutoff(players, roundsRemaining);
        }

//        private int getScoreCutoff(List<Player> players, int roundsRemaining){
//            ArrayList<Integer> scores = new ArrayList<>();
//            for (Player p : players) {
//                int score = p.getScore();
//                scores.add(score);
//            }
//            scores.sort(Comparator.reverseOrder());
//            return scores.get(2) - 2 * roundsRemaining;
//        }


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

        private void addToScoreTable(TreeMap<Integer, HashMap<String, Integer>> a, Player p, int score){
            HashMap<String, Integer> freqMap = a.getOrDefault(score, new HashMap<>());
            freqMap.put("TOTAL",
                    freqMap.getOrDefault("TOTAL", 0) + 1);
            freqMap.put(p.getOrganization(),
                    freqMap.getOrDefault(p.getOrganization(), 0) + 1);
            a.put(score, freqMap);
        }

//        private void addToOrganizationTable(TreeMap<String, HashMap<Integer, Integer>> a, Player p, int score){
//            HashMap<Integer, Integer> freqMap = a.getOrDefault(p.getOrganization(), new HashMap<>());
//            freqMap.put(score,
//                    freqMap.getOrDefault(score, 0) + 1);
//            freqMap.put(-1,
//                    freqMap.getOrDefault(-1, 0) + 1);
//            a.put(p.getOrganization(), freqMap);
//        }

        private int getWeightForFirstRound(VariableState state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            if(bestWeightForFirstRound != Integer.MIN_VALUE && variableIndex.round() != 0)
                return bestWeightForFirstRound;

            int weightForFirstRound = 0;
            HashSet<Integer> firstRoundPlayersPaired = new HashSet<>();
            TreeMap<Integer, HashMap<String, Integer>> scoreToFrequencyTable = new TreeMap<>();
//            TreeMap<String, HashMap<Integer, Integer>> organizationToFrequencyTable = new TreeMap<>();
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
                }
                else //if(playerI.getScore() >= scoreCutoff)
                    addToScoreTable(scoreToFrequencyTable, playerI, playerI.getScore());
//                else
//                    addToOrganizationTable(organizationToFrequencyTable, playerI, playerI.getScore());
            }

            Iterator<Integer> scoreTableIterator = scoreToFrequencyTable.navigableKeySet().descendingIterator();
            int a = -1;
            HashMap<String, Integer> aFreqTable = null;
            while (a != -1 || scoreTableIterator.hasNext()){
                if(a == -1){
                    a = scoreTableIterator.next();
                    aFreqTable = scoreToFrequencyTable.get(a);
                }
                String mostCommonOrg = mostCommonFromOrgTable(aFreqTable, null);
                int numPlayersSameOrgRemaining = aFreqTable.get(mostCommonOrg) * 2 - aFreqTable.get("TOTAL");
                weightForFirstRound += Math.max(numPlayersSameOrgRemaining - aFreqTable.get("TOTAL") % 2, 0) * NORMAL_COST_OF_SAME_ORG;

                if(aFreqTable.get("TOTAL") % 2 == 0){
                    a = -1;
                }
                else if(scoreTableIterator.hasNext()){
                    int nextScore = scoreTableIterator.next();
                    weightForFirstRound += (a-nextScore) * (a-nextScore) * 2 * NORMAL_POINT_FACTOR; //Add the weight of pairing two players with neighbouring scores
                    a = nextScore;
                    aFreqTable = scoreToFrequencyTable.get(nextScore);

                    String mostCommonOrgForNextScore = mostCommonFromOrgTable(aFreqTable, (numPlayersSameOrgRemaining > 0)? mostCommonOrg:null);
                    if(mostCommonOrgForNextScore == null){
                        mostCommonOrgForNextScore = mostCommonFromOrgTable(aFreqTable, null);
                        weightForFirstRound += 2;
                    }

                    aFreqTable.put(mostCommonOrgForNextScore, aFreqTable.get(mostCommonOrgForNextScore) - 1);
                    aFreqTable.put("TOTAL", aFreqTable.get("TOTAL") - 1);
                }
                else {
                    weightForFirstRound += WEIGHT_OF_SIT_OUT + 5 * a * a * NORMAL_POINT_FACTOR;//Sit out the worst player if there are an odd number
                    a = -1;
                }
            }

            if(variableIndex.round() != 0)
                bestWeightForFirstRound = weightForFirstRound;
            return weightForFirstRound;
        }

        private String mostCommonFromOrgTable(HashMap<String, Integer> orgToFreq, String ignoreOrg) {
            int max = 0;
            String mostCommon = null;
            for (String org : orgToFreq.keySet()) {
                if(org.equals("TOTAL") || (ignoreOrg != null && ignoreOrg.equals(org))) continue;
                
                if(orgToFreq.get(org) > max){
                    max = orgToFreq.get(org);
                    mostCommon = org;
                }
            }
            return mostCommon;
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
