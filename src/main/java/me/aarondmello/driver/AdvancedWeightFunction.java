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

    public AdvancedWeightFunction(List<Player> players, int roundsRemaining){
        scoreCutoff = getScoreCutoff(players, roundsRemaining);
    }

    private int getScoreCutoff(List<Player> players, int roundsRemaining){
        ArrayList<Integer> scores = new ArrayList<>();
        for (Player p : players) {
            int score = p.getScore();
            scores.add(score);
        }
        scores.sort(Comparator.reverseOrder());
        return scores.get(2) - 2 * roundsRemaining;
    }
    @Override
    public Constraint getWeightConstraint(int bestWeight) {
        return new AdvancedWeightConstraint(bestWeight);
    }

    @Override
    public int calculateWeight(int opponentIndex, Player player, int roundIndex, List<Player> players) {
        if(opponentIndex == -1){
            if(player.getScore() >= scoreCutoff)
                return (player.hasSatOut()? 2* WEIGHT_OF_SIT_OUT : WEIGHT_OF_SIT_OUT) +
                        ((roundIndex == 0)? player.getScore() * player.getScore() * 5 * NORMAL_POINT_FACTOR : 0);
            return (player.hasSatOut()? 2* WEIGHT_OF_SIT_OUT : WEIGHT_OF_SIT_OUT) +
                    ((roundIndex == 0)? player.getScore() * player.getScore() * 5 * WEAK_POINT_FACTOR : 0);
        }


        Player opponent = players.get(opponentIndex);
        if (roundIndex == 0){
            if(player.getScore() >= scoreCutoff)
                return (player.getScore() - opponent.getScore()) *
                    (player.getScore() - opponent.getScore()) * NORMAL_POINT_FACTOR +
                    (player.getOrganization().equals(opponent.getOrganization())? NORMAL_COST_OF_SAME_ORG:0);
            return (player.getScore() - opponent.getScore()) *
                    (player.getScore() - opponent.getScore()) * WEAK_POINT_FACTOR +
                    (player.getOrganization().equals(opponent.getOrganization())? WEAK_COST_OF_SAME_ORG:0);
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

        private void addToScoreTable(TreeMap<Integer, HashMap<String, Integer>> a, Player p, int score){
            HashMap<String, Integer> freqMap = a.getOrDefault(score, new HashMap<>());
            freqMap.put("TOTAL",
                    freqMap.getOrDefault("TOTAL", 0) + 1);
            freqMap.put(p.getOrganization(),
                    freqMap.getOrDefault(p.getOrganization(), 0) + 1);
            a.put(score, freqMap);
        }

        private int getWeightForFirstRound(VariableState state, VariableIndex variableIndex, VariableAssignment assignment, List<Player> players) {
            if(bestWeightForFirstRound != Integer.MIN_VALUE && variableIndex.round() != 0)
                return bestWeightForFirstRound;

            int weightForFirstRound = 0;
            HashSet<Integer> firstRoundPlayersPaired = new HashSet<>();
            TreeMap<Integer, HashMap<String, Integer>> strongScoreToOrgToCount = new TreeMap<>();
            HashMap<String, Integer> weakOrgToCount = new HashMap<>();
            TreeMap<Integer, Integer> weakScoreToCount = new TreeMap<>();
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
                else if(playerI.getScore() >= scoreCutoff)
                    addToScoreTable(strongScoreToOrgToCount, playerI, playerI.getScore());
                else{
                    weakOrgToCount.put(playerI.getOrganization(), weakOrgToCount.getOrDefault(playerI.getOrganization(), 0) + 1);
                    weakOrgToCount.put(null, -2);
                    weakScoreToCount.put(playerI.getScore(), weakScoreToCount.getOrDefault(playerI.getScore(), 0) + 1);
                }
            }

            Iterator<Integer> strongScoreTableIterator = strongScoreToOrgToCount.navigableKeySet().descendingIterator();
            Iterator<Integer> weakScoreTableIterator = weakScoreToCount.navigableKeySet().descendingIterator();
            int a = -1;
            int x = -1;
            HashMap<String, Integer> aFreqTable = null;
            while (a != -1 || strongScoreTableIterator.hasNext()){
                if(a == -1){
                    a = strongScoreTableIterator.next();
                    aFreqTable = strongScoreToOrgToCount.get(a);
                }
                String mostCommonOrg = mostCommonFromOrgTable(aFreqTable, null);
                int numPlayersSameOrgRemaining = aFreqTable.get(mostCommonOrg) * 2 - aFreqTable.get("TOTAL");
                weightForFirstRound += Math.max(numPlayersSameOrgRemaining - aFreqTable.get("TOTAL") % 2, 0) * NORMAL_COST_OF_SAME_ORG;

                if(aFreqTable.get("TOTAL") % 2 == 0){
                    a = -1;
                }
                else if(strongScoreTableIterator.hasNext()){
                    int nextScore = strongScoreTableIterator.next();
                    weightForFirstRound += (a-nextScore) * (a-nextScore) * 2 * NORMAL_POINT_FACTOR; //Add the weight of pairing two players with neighbouring scores
                    a = nextScore;
                    aFreqTable = strongScoreToOrgToCount.get(nextScore);

                    String mostCommonOrgForNextScore = mostCommonFromOrgTable(aFreqTable, (numPlayersSameOrgRemaining > 0)? mostCommonOrg:null);
                    if(mostCommonOrgForNextScore == null){
                        mostCommonOrgForNextScore = mostCommonFromOrgTable(aFreqTable, null);
                        weightForFirstRound += 2;
                    }

                    aFreqTable.put(mostCommonOrgForNextScore, aFreqTable.get(mostCommonOrgForNextScore) - 1);
                    aFreqTable.put("TOTAL", aFreqTable.get("TOTAL") - 1);
                }
                else if(weakScoreToCount.isEmpty()) {
                    weightForFirstRound += WEIGHT_OF_SIT_OUT + 5 * x * x * NORMAL_POINT_FACTOR;//Sit out the worst player if there are an odd number
                    a = -1;
                }
                else{
                    x = weakScoreTableIterator.next();
                    weightForFirstRound += (x-a) * (x-a) * (NORMAL_POINT_FACTOR + WEAK_POINT_FACTOR);
                    weakScoreToCount.put(x, weakScoreToCount.get(x) - 1);
                    String m = weakOrgToCount.keySet().stream().reduce(null, (b, c) -> (weakOrgToCount.get(b) > weakOrgToCount.get(c))? b:c);
                    int num = weakOrgToCount.values().stream().reduce(0, Integer::sum);
                    weightForFirstRound += Math.max(weakOrgToCount.get(m) - num - aFreqTable.get("TOTAL") % 2, 0) * WEAK_COST_OF_SAME_ORG;
                    a = -1;
                }
            }

            int xFreq = -1;
            while (x != -1 || weakScoreTableIterator.hasNext()){
                if(x == -1){
                    x = weakScoreTableIterator.next();
                    xFreq = weakScoreToCount.get(x);
                }

                if(xFreq % 2 == 0){
                    x = -1;
                }
                else if(weakScoreTableIterator.hasNext()){
                    int b = weakScoreTableIterator.next();
                    weightForFirstRound += (x-b) * (x-b) * 2 * WEAK_POINT_FACTOR; //Add the weight of pairing two players with neighbouring scores
                    x = b;
                    xFreq = weakScoreToCount.get(b) - 1;
                }
                else{
                    weightForFirstRound += WEIGHT_OF_SIT_OUT + 5 * x * x * WEAK_POINT_FACTOR;//Sit out the worst player if there are an odd number
                    x = -1;
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
