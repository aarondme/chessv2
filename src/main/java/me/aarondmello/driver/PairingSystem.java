package me.aarondmello.driver;

import java.util.*;
import java.util.stream.Collectors;

import me.aarondmello.datatypes.*;
interface Constraint {
    Iterable<int[]> applyTo(PairingSystem.State state, List<Player> players);
    String name();
}
interface WeightFunction {
    Constraint getWeightConstraint(int bestWeight);
    int calculateWeight(int opponentIndex, Player p, int roundIndex, List<Player> players);
}
public class PairingSystem extends Thread {
    State bestSolution;
    State initialState;
    int roundsRemaining;
    ArrayList<Player> players;
    int roundNumber;
    int numEntries, numPlayers;
    private int bestWeight = Integer.MAX_VALUE;
    WeightFunction weightFunction;

    @Override
    public void run() {
        gac(initialState);
    }

    public PairingSystem(int roundNumber, ArrayList<Player> players, int totalRounds){
        weightFunction = new BasicWeightFunction();
        this.roundsRemaining = totalRounds - roundNumber + 1;
        this.players = players;
        this.roundNumber = roundNumber;
        this.initialState = new State();
        bestSolution = new State(initialState);
        bestSolution.trivialize();
        numPlayers = players.size();
        numEntries = players.size() * roundsRemaining;
    }

    public static Round pairRound(int roundNumber, ArrayList<Player> players, int totalRounds){
        PairingSystem s = new PairingSystem(roundNumber, players, totalRounds);

        s.start();
        try {
            s.join(80_000);
        }catch (InterruptedException ignored){}
        Round r = s.bestSolution.getRound();
        s.interrupt();

        return r;
    }

    private void gac(State previousState) {
        if(previousState == null) previousState = initialState;

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
            Iterable<int[]> modifiedVariables = constraint.applyTo(state, players);
            if(modifiedVariables == null)
                return false;
            for(int[] coordinate : modifiedVariables){
                List<Constraint> toAdd = getConstraintsForVar(coordinate);
                toAdd.removeIf((d -> constraintNames.contains(d.name())));
                gacQueue.addAll(toAdd);
                constraintNames.addAll(gacQueue.stream().map(Constraint::name).collect(Collectors.toList()));
            }

        }
        return true;
    }

    private LinkedList<Constraint> getConstraintsForVar(int[] coordinate) {
        LinkedList<Constraint> constraints = new LinkedList<>();
        constraints.add(new PlayerConstraint(coordinate[0]));
        constraints.add(new RoundConstraint(coordinate[1]));
        constraints.add(weightFunction.getWeightConstraint(bestWeight));
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

        public boolean setVar(int[] index, int value) {
            return variables[index[0]][index[1]].setValue(value);
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

        public boolean setVar(int playerIndex, int roundIndex, int value) {
            return variables[playerIndex][roundIndex].setValue(value);
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

        public boolean setValue(int opponentIndex) {
            return values.removeIf(varAssignment -> varAssignment.opponentIndex != opponentIndex);
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

    record VarAssignment(int opponentIndex, int weight) { }

    private class PlayerConstraint implements Constraint {
        //Each player does not have the same opponent more than once
        private final int playerIndex;
        private final String name;
        public PlayerConstraint(int playerIndex) {
            this.playerIndex = playerIndex;
            this.name = "h" + playerIndex;
        }


        @Override
        public Iterable<int[]> applyTo(State state, List<Player> players) {
            LinkedList<int[]> modified = new LinkedList<>();
            HashSet<Integer> usedValues = new HashSet<>();
            for (int i = 0; i < roundsRemaining; i++) {
                Variable w = state.getVar(playerIndex, i);
                int wVal = w.getValue();
                if(w.isSingleton() && wVal != -1)
                    usedValues.add(wVal);
            }

            for (int i = 0; i < roundsRemaining; i++) {
                Variable w = state.getVar(playerIndex, i);
                if(w.isSingleton()) continue;
                List<VarAssignment> domain = w.getDomain();

                if(domain.removeIf(v -> usedValues.contains(v.opponentIndex))){
                    if(domain.isEmpty()) return null;
                    modified.add(new int[]{playerIndex, i});
                }
            }
            return modified;
        }

        @Override
        public String name() {
            return name;
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
        public Iterable<int[]> applyTo(State state, List<Player> players) {
            LinkedList<int[]> modified = new LinkedList<>();
            HashSet<Integer> usedValues = new HashSet<>();
            for (int i = 0; i < numPlayers; i++) {
                Variable w = state.getVar(i, roundIndex);
                int wVal = w.getValue();
                if(w.isSingleton() && wVal != -1){
                    usedValues.add(wVal);
                    usedValues.add(i);
                    if(state.setVar(wVal, roundIndex, i)){
                        if(state.getVar(wVal, roundIndex).isEmpty()) return null;
                        modified.add(new int[]{wVal, roundIndex});
                    }
                }
            }

            for (int i = 0; i < numPlayers; i++) {
                Variable w = state.getVar(i, roundIndex);
                if(w.isSingleton()) continue;
                List<VarAssignment> domain = w.getDomain();

                if(domain.removeIf(v -> usedValues.contains(v.opponentIndex))){
                    if(domain.isEmpty()) return null;
                    modified.add(new int[]{i, roundIndex});
                }
            }
            return modified;
        }

        @Override
        public String name() {
            return name;
        }
    }
}
