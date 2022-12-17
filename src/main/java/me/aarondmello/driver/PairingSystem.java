package me.aarondmello.driver;

import java.util.*;
import java.util.stream.Collectors;

import me.aarondmello.datatypes.*;

public class PairingSystem {
    State bestSolution = null;
    int roundsRemaining;
    ArrayList<Player> players;
    int roundNumber;
    int numEntries, numPlayers;
    private int bestWeight = Integer.MAX_VALUE;
    WeightFunction weightFunction;

    public PairingSystem(){
        weightFunction = new BasicWeightFunction();
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


    private Game pairPlayers(Player p, Player q) {
        if (p.getGamesAsBlack() >= q.getGamesAsBlack())
            return new Game(p, q);
        return new Game(q, p);
    }

    public class State {
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

    public class Variable {
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
                values.add(new VarAssignment(opponentIndex, weightFunction.calculateWeight(opponentIndex, p, roundIndex, players)));
            }
            values.sort(Comparator.comparing(varAssignment -> varAssignment.weight));
            values.addLast(new VarAssignment(-1, weightFunction.calculateWeight(-1, p, roundIndex, players)));
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
            //weight must be an underestimate of the best weight possible given currently assigned variables
            return weightFunction.getBestWeightPossible(state, coordinate, pos.opponentIndex, pos.weight, players) < bestWeight;
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
