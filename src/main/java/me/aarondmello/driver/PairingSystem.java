package me.aarondmello.driver;

import java.util.*;
import java.util.stream.Collectors;

import me.aarondmello.datatypes.*;

public class PairingSystem {
    State bestSolution = null;
    int totalRounds;
    ArrayList<Player> players;
    int roundNumber;

    public PairingSystem(){
    }

    public Round pairRound(int roundNumber, ArrayList<Player> players, int totalRounds){
        this.totalRounds = totalRounds;
        this.players = players;
        this.roundNumber = roundNumber;
        bestSolution = getDefaultState();
        State vars = new State();
        gac(vars);
        return bestSolution.getRound(roundNumber);
    }

    private State gac(State vars) {
        State myState = vars.copy();
        int[] index = myState.getUnassignedVariable();
        if(index == null)
            return myState;

        for(VarAssignment pos : vars.getVar(index).getDomain()){
            myState.setVar(index, pos.opponentIndex);

            LinkedList<Constraint> constraints = getConstraintsForVar(index);
            HashSet<String> constraintName = constraints.stream().map(Constraint::name).collect(Collectors.toCollection(HashSet::new));

            if(gacEnforce(constraints, constraintName, myState)){
                State sol = gac(myState);
                if(sol != null)
                    bestSolution = sol;
            }

            myState = vars.copy();
        }
        return bestSolution;
    }

    private State getDefaultState() {
        State myState = new State();
        myState.trivialize();
        return myState;
    }

    private boolean gacEnforce(LinkedList<Constraint> constraints, HashSet<String> constraintNames, State state) {
        while (!constraints.isEmpty()){
            Constraint c = constraints.removeFirst();
            constraintNames.remove(c.name());
            for(int[] x : c){
                Variable v = state.getVar(x);
                if(state.removeFailingConstraint(c, x)){
                    if(v.isEmpty())
                        return false;
                    List<Constraint> toAdd = getConstraintsForVar(x);
                    toAdd.removeIf((d -> constraintNames.contains(d.name())));
                    constraints.addAll(toAdd);
                    constraintNames.addAll(constraints.stream().map(Constraint::name).collect(Collectors.toList()));
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

    private class State {
        Variable[][] variables;
        int weight = 0;

        State(){
            variables = new Variable[players.size()][totalRounds];
            for(int i = 0; i < players.size(); i++){
                LinkedList<PlayerGameSummary> summaries = players.get(i).getPlayerGameSummaries();
                for(int j = 0; j < summaries.size(); j++){
                    variables[i][j] = new Variable(players.size(), i, j);
                    int opponentIndex = -1;
                    int opponentId = summaries.get(j).getOpponent().getID();
                    for (int k = 0; k < players.size(); k++)
                        if(players.get(k).getID() == opponentId){
                            opponentIndex = k;
                            break;
                        }
                    }

                    variables[i][j].setValue(opponentIndex);
                }
                for (int j = summaries.size(); j < totalRounds; j++)
                    variables[i][j] = new Variable(players.size(), i, j);
            }

            for (Variable[] vars: variables)
                for (Variable v: vars)
                    weight += v.getDomain().get(0).weight;
        }

        public State copy() {
            State abc = new State();
            abc.variables = new Variable[variables.length][variables[0].length];
            for(int i = 0; i < variables.length; i++)
                for (int j = 0; j < variables[i].length; j++)
                    abc.variables[i][j] = new Variable(variables[i][j]);
            abc.weight = weight;
            return abc;
        }

        public boolean removeFailingConstraint(Constraint c, int[] coordinate){
            List<VarAssignment> domain = variables[coordinate[0]][coordinate[1]].values;
            int minVal = domain.get(0).weight;
            boolean didRemove = domain.removeIf(pos -> !c.hasAssignment(this, coordinate, pos));
            if(!domain.isEmpty())
                weight += domain.get(0).weight - minVal;
            return didRemove;
        }

        public void trivialize() {
            for (Variable[] variable : variables)
                for (Variable value : variable)
                    if (!value.isSingleton()){
                        int minVal = value.values.get(0).weight;
                        value.setValue(-1);
                        weight += value.values.get(0).weight - minVal;
                    }

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
        public Variable getVar(int a, int b) {
            return variables[a][b];
        }

        public Round getRound(int roundNumber) {
            HashSet<Integer> pairedIds = new HashSet<>();
            Round r = new Round();
            for(int i = 0; i < variables.length; i++){
                if(pairedIds.contains(i))
                    continue;
                int opponent = variables[i][roundNumber - 1].getValue();
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

        public int getWeight() {
            int weight = 0;
            for (Variable[] variable : variables) {
                for (Variable value : variable) {
                    weight += value.values.get(0).weight;
                }
            }
            return weight;
        }

        public void setVar(int[] index, int pos) {
            variables[index[0]][index[1]].setValue(pos);
        }

        public int getWeightOf(int i, int j) {
            return variables[i][j].values.get(0).weight;
        }
    }

    private class Variable {
        LinkedList<VarAssignment> values;
        public Variable(Variable x){
            values = new LinkedList<>();
            values.addAll(x.values);
        }

        public Variable(int numPlayers, int playerIndex, int roundIndex) {
            values = new LinkedList<>();
            Player p = players.get(playerIndex);
            for(int opponentIndex = 0; opponentIndex < numPlayers; opponentIndex++){
                if(opponentIndex == playerIndex) continue;
                values.add(new VarAssignment(opponentIndex, calculateWeight(players.get(opponentIndex), p, roundIndex)));
            }
            values.add(new VarAssignment(-1,
                    p.hasSatOut()? 2_000_000:1_000_000 + p.getScore() * p.getScore() * 5));
            values.sort(Comparator.comparing(varAssignment -> varAssignment.weight));
        }

        private int calculateWeight(Player opponent, Player player, int rounds) {
            if (rounds == roundNumber - 1) {
                return (player.getScore() - opponent.getScore()) *
                            (player.getScore() - opponent.getScore());
            }
            return 0;
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

        public void removeValue(int pos) {
            values.removeIf(varAssignment -> varAssignment.var == pos);
        }

        public boolean isSingleton() {
            return values.size() == 1;
        }

        public int getValue(){
            if(isSingleton())
                return values.get(0).opponentIndex;

            return -1;
        }

        public boolean isUnassigned() {
            return values.size() > 1;
        }

        public boolean isEmpty() {
            return values.isEmpty();
        }


    }

    private record VarAssignment(int opponentIndex, int weight) {

    }
    private interface Constraint {
        int[][] scope();
        boolean hasAssignment(State state, Variable v, VarAssignment pos);
        String name();
    }

    private class PlayerConstraint implements Constraint {


        private final int playerIndex;
        private final String name;
        public PlayerConstraint(int playerIndex) {
            this.playerIndex = playerIndex;
            this.name = "h" + playerIndex;
        }

        @Override
        public int[][] scope() {
            int[][] s = new int[totalRounds][];
            for (int i = 0; i < totalRounds; i++) {
                s[i] = new int[]{x, i};
            }
            return s;
        }

        @Override
        public boolean hasAssignment(State state, int[] coordinate, VarAssignment pos) {
            int numSitOuts = (pos.opponentIndex == -1)? 1:0;

            for (int i = 0; i < totalRounds; i++) {
                Variable w = state.getVar(playerIndex, i);
                if(i == coordinate[1] || !w.isSingleton()) continue;

                if(w.contains(-1))
                    numSitOuts++;
                else if(w.contains(pos.opponentIndex))
                    return false;
            }
            return numSitOuts <= (totalRounds + 1)/2;
        }

        @Override
        public String name() {
            return name;
        }
    }

    private class RoundConstraint implements Constraint {
        private final int roundIndex;
        private final String name;
        public RoundConstraint(int roundIndex) {
            this.roundIndex = roundIndex;
            this.name = "v" + roundIndex;
        }

        @Override
        public int[][] scope() {
            int[][] s = new int[players.size()][];
            for (int i = 0; i < players.size(); i++) {
                s[i] = new int[]{i, y};
            }
            return s;
        }

        @Override
        public boolean hasAssignment(State state, int[] coordinate, VarAssignment pos) {
            int numSitOuts = (pos.opponentIndex == -1)? 1:0;

            for (int i = 0; i < players.size(); i++) {
                Variable w = state.getVar(i, roundIndex);
                if(i == pos.opponentIndex && !w.contains(coordinate[0])) return false;
                if(i == coordinate[0] || !w.isSingleton()) continue;

                if(w.contains(-1)) numSitOuts++;
                else if(w.contains(pos.opponentIndex)) return false;
            }
            return numSitOuts <= roundIndex +1;
        }

        @Override
        public String name() {
            return name;
        }
    }

    private class WeightConstraint implements Constraint {

        @Override
        public int[][] scope() {
            int[][] s = new int[players.size() * totalRounds][];
            int w = 0;
            for (int i = 0; i < players.size(); i++) {
                for (int j = 0; j < totalRounds; j++) {
                    s[w] = new int[]{i, j};
                    w++;
                }
            }
            return s;
        }

        @Override
        public boolean hasAssignment(State state, int[] coordinate, VarAssignment pos) {
            int weight = pos.weight - state.getWeightOf(coordinate[0], coordinate[1]);
            weight += state.getWeight();
            return weight < bestSolution.getWeight();
        }

        @Override
        public String name() {
            return "w";
        }
    }
}
