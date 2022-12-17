package me.aarondmello.driver;

import java.util.*;
import java.util.stream.Collectors;

import me.aarondmello.datatypes.*;

public class PairingSystem {
    private static final int WEIGHT_OF_SIT_OUT = 1_000_000;
    State bestSolution = null;
    int roundsRemaining;
    ArrayList<Player> players;
    int roundNumber;
    int numEntries, numPlayers;
    private int bestWeight = Integer.MAX_VALUE;

    public PairingSystem(){
    }

    public Round pairRound(int roundNumber, ArrayList<Player> players, int totalRounds){
        this.roundsRemaining = totalRounds - roundNumber + 1;
        this.players = players;
        this.roundNumber = roundNumber;
        State initialState = new State();
        bestSolution = new State(initialState);
        bestSolution.trivialize();
        numPlayers = players.size();
        numEntries = players.size() * roundsRemaining;
        gac(initialState);
        return bestSolution.getRound();
    }

    private void gac(State previousState) {
        int[] index = previousState.getUnassignedVariable();
        if(index == null){
            bestSolution = previousState;
            bestWeight = 0;
            for (Variable[] vars: bestSolution.variables)
                for (Variable v: vars)
                    bestWeight += v.getDomain().get(0).weight;

            return;
        }

        for(VarAssignment pos : previousState.getVar(index).getDomain()){
            State nextState = new State(previousState);
            nextState.setVar(index, pos.opponentIndex);

            LinkedList<Constraint> constraints = getConstraintsForVar(index);
            HashSet<String> constraintNames = constraints.stream().map(Constraint::name)
                    .collect(Collectors.toCollection(HashSet::new));

            if(gacEnforce(constraints, constraintNames, nextState))
                gac(nextState);
        }
    }

    private boolean gacEnforce(LinkedList<Constraint> gacQueue, HashSet<String> constraintNames, State state) {
        while (!gacQueue.isEmpty()){
            Constraint constraint = gacQueue.removeFirst();
            constraintNames.remove(constraint.name());
            for(int[] coordinate : constraint){
                if(state.removeFailingConstraint(constraint, coordinate)){
                    if(state.getVar(coordinate).isEmpty())
                        return false;
                    List<Constraint> toAdd = getConstraintsForVar(coordinate);
                    toAdd.removeIf((d -> constraintNames.contains(d.name())));
                    gacQueue.addAll(toAdd);
                    constraintNames.addAll(gacQueue.stream().map(Constraint::name).collect(Collectors.toList()));
                }
            }
        }
        return true;
    }

    private LinkedList<Constraint> getConstraintsForVar(int[] coordinate) {
        LinkedList<Constraint> constraints = new LinkedList<>();
        constraints.add(new PlayerConstraint(coordinate[0]));
        constraints.add(new RoundConstraint(coordinate[1]));
        constraints.add(new WeightConstraint());
        return constraints;
    }

    private int calculateWeight(int opponentIndex, Player player, int roundIndex) {
        if(opponentIndex == -1)
            return (player.hasSatOut()? 2* WEIGHT_OF_SIT_OUT : WEIGHT_OF_SIT_OUT) +
                    ((roundIndex == 0)? player.getScore() * player.getScore() * 5 : 0);

        Player opponent = players.get(opponentIndex);
        if (roundIndex == 0)
            return (player.getScore() - opponent.getScore()) *
                    (player.getScore() - opponent.getScore());
        return 0;
    }


    private Game pairPlayers(Player p, Player q) {
        if (p.getGamesAsBlack() >= q.getGamesAsBlack())
            return new Game(p, q);
        return new Game(q, p);
    }

    private class State {
        Variable[][] variables;

        State(){
            variables = new Variable[players.size()][roundsRemaining];
            for(int i = 0; i < players.size(); i++)
                for (int j = 0; j < roundsRemaining; j++)
                    variables[i][j] = new Variable(players.size(), i, j);

        }

        public State(State stateToCopy){
            variables = new Variable[stateToCopy.variables.length][stateToCopy.variables[0].length];
            for(int i = 0; i < variables.length; i++)
                for (int j = 0; j < variables[i].length; j++)
                    variables[i][j] = new Variable(stateToCopy.variables[i][j]);
        }



        public boolean removeFailingConstraint(Constraint c, int[] coordinate){
            List<VarAssignment> domain = variables[coordinate[0]][coordinate[1]].values;
            return domain.removeIf(pos -> !c.hasAssignment(this, coordinate, pos));
        }

        public void trivialize() {
            for (Variable[] variable : variables)
                for (Variable value : variable)
                    if (!value.isSingleton())
                        value.setValue(-1);

        }

        public int[] getUnassignedVariable() {
            for (int j = 0; j < variables[0].length; j++)
                for (int i = 0; i < variables.length; i++)
                    if(variables[i][j].isUnassigned())
                        return new int[]{i, j};
            return null;
        }

        public Variable getVar(int[] index) {
            return variables[index[0]][index[1]];
        }
        public Variable getVar(int playerIndex, int roundIndex) {
            return variables[playerIndex][roundIndex];
        }

        public Round getRound() {
            HashSet<Integer> pairedIds = new HashSet<>();
            Round r = new Round();
            for(int i = 0; i < variables.length; i++){
                if(pairedIds.contains(i))
                    continue;
                int opponent = variables[i][0].getValue();
                if(opponent == -1){
                    r.addGame(new Game(players.get(i), NullPlayer.getInstance()));
                    pairedIds.add(i);
                }
                else{
                    r.addGame(pairPlayers(players.get(i), players.get(opponent)));
                    pairedIds.add(i);
                    pairedIds.add(opponent);
                }
            }
            return r;
        }

        public void setVar(int[] index, int pos) {
            variables[index[0]][index[1]].setValue(pos);
        }

        public int getWeightOf(int playerIndex, int roundIndex) {
            return variables[playerIndex][roundIndex].values.get(0).weight;
        }

        public int numSitOuts(int r) {
            int num = 0;
            for (Variable[] variable : variables)
                if (variable[r].isSingleton() && variable[r].getValue() == -1)
                    num++;
            return num;
        }
    }

    private class Variable {
        LinkedList<VarAssignment> values;
        public Variable(Variable x){
            values = new LinkedList<>(x.values);
        }

        public Variable(int numPlayers, int playerIndex, int roundIndex) {
            values = new LinkedList<>();
            Player p = players.get(playerIndex);
            for(int opponentIndex = 0; opponentIndex < numPlayers; opponentIndex++){
                if(opponentIndex == playerIndex || p.hasPlayedAgainst(players.get(opponentIndex)))
                    continue;
                values.add(new VarAssignment(opponentIndex, calculateWeight(opponentIndex, p, roundIndex)));
            }
            values.sort(Comparator.comparing(varAssignment -> varAssignment.weight));
            values.addLast(new VarAssignment(-1, calculateWeight(-1, p, roundIndex)));
        }



        public LinkedList<VarAssignment> getDomain() {
            return values;
        }

        public void setValue(int opponentIndex) {
            values.removeIf(varAssignment -> varAssignment.opponentIndex != opponentIndex);
        }

        public boolean contains(int opponentIndex){
            return values.stream().anyMatch(v -> v.opponentIndex == opponentIndex);
        }

        public boolean isSingleton() {
            return values.size() == 1;
        }

        public int getValue(){
            return isSingleton()? values.get(0).opponentIndex : -1;
        }

        public boolean isUnassigned() {
            return values.size() > 1;
        }

        public boolean isEmpty() {
            return values.isEmpty();
        }
    }

    private record VarAssignment(int opponentIndex, int weight) { }

    private interface Constraint extends Iterable<int[]> {
        boolean hasAssignment(State state, int[] coordinate, VarAssignment pos);
        String name();
    }

    private class PlayerConstraint implements Constraint {
        //Each player does not have the same opponent more than once
        private final int playerIndex;
        private final String name;
        public PlayerConstraint(int playerIndex) {
            this.playerIndex = playerIndex;
            this.name = "h" + playerIndex;
        }
        @Override
        public Iterator<int[]> iterator() {
            return new ScopeIterator();
        }

        @Override
        public boolean hasAssignment(State state, int[] coordinate, VarAssignment pos) {
            int numSitOuts = (pos.opponentIndex == -1)? 1:0;

            for (int i = 0; i < roundsRemaining; i++) {
                Variable w = state.getVar(playerIndex, i);
                if(i == coordinate[1] || !w.isSingleton()) continue;

                int wOpponentIndex = w.getDomain().get(0).opponentIndex;

                if(wOpponentIndex == -1)
                    numSitOuts++;
                else if(wOpponentIndex == pos.opponentIndex)
                    return false;
            }
            return numSitOuts <= (roundsRemaining + 1)/2;
        }

        @Override
        public String name() {
            return name;
        }

        private class ScopeIterator implements Iterator<int[]>{
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < roundsRemaining;
            }

            @Override
            public int[] next() {
                index += 1;
                return new int[]{playerIndex, index - 1};
            }
        }
    }

    private class RoundConstraint implements Constraint {
        //In a round, each player plays at most one game.
        private final int roundIndex;
        private final String name;
        public RoundConstraint(int roundIndex) {
            this.roundIndex = roundIndex;
            this.name = "v" + roundIndex;
        }
        @Override
        public Iterator<int[]> iterator() {
            return new ScopeIterator();
        }

        @Override
        public boolean hasAssignment(State state, int[] coordinate, VarAssignment pos) {
            int numSitOuts = (pos.opponentIndex == -1)? 1:0;

            for (int i = 0; i < players.size(); i++) {
                Variable w = state.getVar(i, roundIndex);
                if(i == pos.opponentIndex && !w.contains(coordinate[0])) return false;
                if(i == coordinate[0] || !w.isSingleton()) continue;

                int wOpponentIndex = w.getDomain().get(0).opponentIndex;

                if(wOpponentIndex == -1) numSitOuts++;
                else if(wOpponentIndex == pos.opponentIndex) return false;
                else if(wOpponentIndex == coordinate[0] && pos.opponentIndex != i) return false;
            }
            return numSitOuts <= roundNumber + roundIndex;
        }

        @Override
        public String name() {
            return name;
        }
        private class ScopeIterator implements Iterator<int[]>{
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < numPlayers;
            }

            @Override
            public int[] next() {
                index += 1;
                return new int[]{index - 1, roundIndex};
            }
        }
    }

    private class WeightConstraint implements Constraint {
        //The current solution must be better than the best solution to continue
        @Override
        public Iterator<int[]> iterator() {
            return new ScopeIterator();
        }

        @Override
        public boolean hasAssignment(State state, int[] coordinate, VarAssignment pos) {
            int weight = pos.weight;

            HashSet<Integer> firstRoundPlayersPaired = new HashSet<>();
            TreeMap<Integer, Integer> scoreToFreq = new TreeMap<>();
            if(coordinate[1] == 0 && pos.opponentIndex != -1){
               firstRoundPlayersPaired.add(coordinate[0]);
               firstRoundPlayersPaired.add(pos.opponentIndex);
               weight += calculateWeight(coordinate[0], players.get(pos.opponentIndex), 0);
            }
            for (int i = 0; i < players.size(); i++) {
                if(firstRoundPlayersPaired.contains(i)) continue;

                if(state.getVar(i, 0).isSingleton()){
                    weight += state.getWeightOf(i, 0);
                    if (state.getVar(i, 0).getValue() != -1) {
                        weight += calculateWeight(i, players.get(state.getVar(i, 0).getValue()), 0);
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
                    weight += (a-b) * (a-b) * 2; //Add the weight of pairing two players with neighbouring scores
                    a = b;
                    aFreq = scoreToFreq.get(b) - 1;
                }
                else{
                    weight += WEIGHT_OF_SIT_OUT + 5 * a * a;//Sit out the worst player if there are an odd number
                    a = -1;
                }
            }

            for(int r = 1; r < roundsRemaining; r++){
                for (int i = 0; i < players.size(); i++) {
                    if(i == coordinate[0] && r == coordinate[1]) continue;
                    if(state.getVar(i, r).isSingleton() && state.getVar(i, r).getValue() == -1) {
                        weight += state.getWeightOf(i, r);
                    }
                }
                int numPlayersNotSittingOut = (players.size() - state.numSitOuts(r)) + ((r == coordinate[1] && pos.opponentIndex == -1)? 1:0);
                if(numPlayersNotSittingOut % 2 == 1) //If an odd number of players are not marked as sitting out in a round, we will find one more
                    weight += WEIGHT_OF_SIT_OUT;
            }

            return weight < bestWeight; //weight must be an underestimate of the best weight possible given currently assigned variables
        }

        @Override
        public String name() {
            return "w";
        }
        private class ScopeIterator implements Iterator<int[]>{
            int playerIndex = 0;
            int roundIndex = 0;
            int total = 0;

            @Override
            public boolean hasNext() {
                return total < numEntries;
            }

            @Override
            public int[] next() {
                if(playerIndex == numPlayers){
                    roundIndex++;
                    playerIndex = 1;
                }
                else
                    playerIndex += 1;
                total += 1;
                return new int[]{playerIndex - 1, roundIndex};
            }
        }
    }
}
