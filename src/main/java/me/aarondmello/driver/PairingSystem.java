package me.aarondmello.driver;

import java.util.*;

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
        gac(vars, 0);
        return bestSolution.getRound(roundNumber);
    }

 

    private State gac(State vars, int depth) {
        State myState = vars.copy();
        int[] index = myState.getUnassignedVariable();
        if(index == null)
            return myState;

        for(int pos : vars.getVar(index[0], index[1]).getDomain()){
            myState.setVar(index[0], index[1], pos);

            LinkedList<Constraint> constraints = getConstraintsForVar(index[0], index[1]);
            HashSet<String> constraintName = new HashSet<>();
            for(Constraint c : constraints)
                constraintName.add(c.name());
            if(gacEnforce(constraints, constraintName, myState)){
                State sol = gac(myState, depth + 1);
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
            for(int[] x : c.scope()){
                Variable v = state.getVar(x[0], x[1]);
                int domainSize = v.getDomain().size();
                v.getDomain().removeIf(pos -> !c.hasAssignment(state, v, pos));
                if(v.isEmpty())
                    return false;
                if(v.getDomain().size() < domainSize){
                    List<Constraint> toAdd = getConstraintsForVar(x[0], x[1]);
                    toAdd.removeIf((d -> constraintNames.contains(d.name())));
                    constraints.addAll(toAdd);
                    for(Constraint d : toAdd)
                        constraintNames.add(d.name());
                }
            }
        }
        return true;
    }

    private LinkedList<Constraint> getConstraintsForVar(int x, int y) {
        LinkedList<Constraint> constraints = new LinkedList<>();
        constraints.add(new HorizontalConstraint(x));
        constraints.add(new VerticalConstraint(y));
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

        State(){
            variables = new Variable[players.size()][totalRounds];
            for(int i = 0; i < players.size(); i++){
                LinkedList<PlayerGameSummary> summaries = players.get(i).getPlayerGameSummaries();
                for(int j = 0; j < summaries.size(); j++){
                    variables[i][j] = new Variable(0);
                    int opponentIndex = -1;
                    for (int k = 0; k < players.size(); k++) {
                        if(players.get(k).getID() == summaries.get(j).getOpponent().getID())
                            opponentIndex = k;
                    }

                    variables[i][j].setValue(opponentIndex);
                }
                for (int j = summaries.size(); j < totalRounds; j++){
                    variables[i][j] = new Variable(players.size());
                    variables[i][j].removeValue(i);
                }
            }
        }

        public State copy() {
            State abc = new State();
            abc.variables = new Variable[variables.length][variables[0].length];
            for(int i = 0; i < variables.length; i++)
                for (int j = 0; j < variables[i].length; j++)
                    abc.variables[i][j] = new Variable(variables[i][j]);

            return abc;
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
            for (int i = 0; i < variables.length; i++) {
                for (int j = 0; j < variables[i].length; j++) {
                    Variable w = getVar(i, j);
                    int minWeight = Integer.MAX_VALUE;
                    for(int index : w.getDomain()){
                        int x = 0;
                        if(index == -1)
                            x += ((players.get(i).hasSatOut())? 2_000_000: 1_000_000);
                        if(j == roundNumber - 1){
                            if(index == -1)
                                x += 5 * players.get(i).getScore() * players.get(i).getScore();
                            else
                                x += (players.get(i).getScore() - players.get(index).getScore()) *
                                        (players.get(i).getScore() - players.get(index).getScore());
                        }
                        if(x < minWeight) minWeight = x;
                    }
                    weight += minWeight;
                }
            }
            return weight;
        }

        public void setVar(int index, int index1, int pos) {
            variables[index][index1].setValue(pos);
        }

    }

    private static class Variable {
        LinkedList<Integer> values;
        public Variable(Variable x){
            values = new LinkedList<>();
            values.addAll(x.values);
        }

        public Variable(int numPlayers) {
            values = new LinkedList<>();
            for(int x = 0; x < numPlayers; x++){
                values.add(x);
            }
            values.add(-1);
        }

        public LinkedList<Integer> getDomain() {
            return values;
        }

        public void setValue(int pos) {
            values.clear();
            values.add(pos);
        }

        public boolean contains(int pos){
            return values.contains(pos);
        }

        public void removeValue(int pos) {
            values.removeIf(value -> value == pos);
        }

        public boolean isSingleton() {
            return values.size() == 1;
        }

        public int getValue(){
            if(isSingleton()){
                for(int v : values){
                    return v;
                }
            }
            return -1;
        }

        public boolean isUnassigned() {
            return values.size() > 1;
        }

        public boolean isEmpty() {
            return values.isEmpty();
        }
    }

    private interface Constraint {
        int[][] scope();
        boolean hasAssignment(State state, Variable v, int pos);
        String name();
    }

    private class HorizontalConstraint implements Constraint {
        int x;
        public HorizontalConstraint(int x) {
            this.x = x;
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
        public boolean hasAssignment(State state, Variable v, int pos) {
            int numSitOuts = (pos == -1)? 1:0;

            for (int i = 0; i < totalRounds; i++) {
                Variable w = state.getVar(x, i);
                if(w.equals(v) || !w.isSingleton()) continue;

                if(w.contains(-1))
                    numSitOuts++;
                else if(w.contains(pos))
                    return false;
            }
            return numSitOuts <= (totalRounds + 1)/2;
        }

        @Override
        public String name() {
            return "h" + x;
        }
    }

    private class VerticalConstraint implements Constraint {
        private final int y;

        public VerticalConstraint(int y) {
            this.y = y;
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
        public boolean hasAssignment(State state, Variable v, int pos) {
            int numSitOuts = (pos == -1)? 1:0;
            int indexOfV = -2;
            for(int i = 0; i < players.size(); i++){
                if(state.getVar(i, y).equals(v)){
                    indexOfV = i;
                    break;
                }
            }

            for (int i = 0; i < players.size(); i++) {
                Variable w = state.getVar(i, y);
                if(i == pos && !w.contains(indexOfV)) return false;
                if(i == indexOfV || !w.isSingleton()) continue;

                if(w.contains(-1)) numSitOuts++;
                else if(w.contains(pos)) return false;
            }
            return numSitOuts <= y+1;
        }

        @Override
        public String name() {
            return "v" + y;
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
        public boolean hasAssignment(State state, Variable v, int pos) {

            int weight = 0;
            for (int i = 0; i < state.variables.length; i++) {
                for (int j = 0; j < state.variables[i].length; j++) {
                    Variable w = state.getVar(i, j);
                    if(w.equals(v)){
                        int x ;
                        if(pos == -1)
                            x = ((players.get(i).hasSatOut())? 2_000_000: 1_000_000);
                        else
                            x = (players.get(i).getScore() - players.get(pos).getScore()) * (players.get(i).getScore() - players.get(pos).getScore());

                        weight += x;
                        continue;
                    }
                    int minWeight = Integer.MAX_VALUE;
                    for(int index : w.getDomain()){
                        int x;
                        if(index == -1)
                            x = ((players.get(i).hasSatOut())? 2_000_000: 1_000_000);
                        else if(j == roundNumber - 1)
                            x = (players.get(i).getScore() - players.get(index).getScore()) * (players.get(i).getScore() - players.get(index).getScore());
                        else
                            x = 0;

                        if(x < minWeight) minWeight = x;
                    }
                    weight += minWeight;
                }
            }
            return weight < bestSolution.getWeight();
        }

        @Override
        public String name() {
            return "w";
        }
    }
}
