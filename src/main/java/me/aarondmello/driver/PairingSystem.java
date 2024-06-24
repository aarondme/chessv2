package me.aarondmello.driver;

import me.aarondmello.datatypes.Game;
import me.aarondmello.datatypes.GameResult;
import me.aarondmello.datatypes.NullPlayer;
import me.aarondmello.datatypes.Player;

import java.util.*;
import java.util.stream.Collectors;
interface Constraint {
    Iterable<VariableIndex> applyTo(VariableState state, List<Player> players);
    String name();
}
interface WeightFunction {
    Constraint getWeightConstraint(int bestWeight);
    int calculateWeight(int opponentIndex, Player p, int roundIndex, List<Player> players);
}
record VariableAssignment(int opponentIndex, int weight) {}
record VariableIndex(int player, int round) {}

/**
 * Runs the pairing algorithm (gac; or generalized arc consistency). Pairs players following these rules:
 * 1. No two players can play each other more than once.
 * 2. A pairing with the least "weight" as determined by the weight function is chosen. Weight functions do the
 * following:
 * a) add a lot of weight for a player sitting out, even more if the player sat out several times.
 * b) add weight corresponding to the difference of scores of the players who are paired with each other
 * c) (optionally) add a small weight if players are from the same school.
 * 3. Players play about as many games as white as they do as black.
 *
 * Note: the number of rounds is taken into account, a pairing which may immediately be of less weight may be avoided
 * if it will force future rounds to have more players sitting out.
 */
public class PairingSystem extends Thread {
    VariableState bestSolution, initialState;
    int roundsRemaining;
    ArrayList<Player> players;
    private int bestWeight = Integer.MAX_VALUE;
    WeightFunction weightFunction;

    public static WeightFunction getWeightFunction(boolean isRegional, ArrayList<Player> activePlayers, int roundNumber, int totalRounds) {
        if(!isRegional)
            return new BasicWeightFunction();
        return new AdvancedWeightFunction(activePlayers, totalRounds - roundNumber + 1);
    }

    @Override
    public void run() {
        gac(initialState);
    }

    public PairingSystem(int roundNumber, ArrayList<Player> players, int totalRounds, WeightFunction function){
        weightFunction = function;
        this.roundsRemaining = totalRounds - roundNumber + 1;
        this.players = players;
        this.initialState = new VariableState(players, roundsRemaining, function);
        bestSolution = new VariableState(initialState);
        bestSolution.trivialize();
    }

    public static List<Game> pairRound(int roundNumber, ArrayList<Player> players, int totalRounds){
        return pairRound(roundNumber, players, new ArrayList<>(), totalRounds, new BasicWeightFunction());
    }

    public static List<Game> pairRound(int roundNumber, ArrayList<Player> players, int totalRounds, WeightFunction weightFunction){
        return pairRound(roundNumber, players, new ArrayList<>(), totalRounds, weightFunction);
    }


    public static List<Game> pairRound(int roundNumber, ArrayList<Player> players, Iterable<Player> inactivePlayers, int totalRounds, WeightFunction function){
        PairingSystem s = new PairingSystem(roundNumber, players, totalRounds, function);
        s.start();
        try {s.join(30_000);}
        catch (InterruptedException ignored){}
        LinkedList<Game> r = getRound(s.bestSolution);
        s.interrupt();
        for (Player p: inactivePlayers) {
            Game g = new Game(p, NullPlayer.getInstance());
            g.setResult(GameResult.BLACK_WIN);
            r.add(g);
        }
        return r;
    }

    public static LinkedList<Game> getRound(VariableState bestSolution) {
        HashSet<Integer> pairedIds = new HashSet<>();
        LinkedList<Game> r = new LinkedList<>();
        for(int i = 0; i < bestSolution.players.size(); i++){
            if(pairedIds.contains(i))
                continue;
            int opponent = bestSolution.variables.get(new VariableIndex(i, 0)).get(0).opponentIndex();
            if(opponent == -1){
                Game g = new Game(bestSolution.players.get(i), NullPlayer.getInstance());
                g.setResult(GameResult.WHITE_WIN);
                r.add(g);
                pairedIds.add(i);
            }
            else{
                r.add(pairPlayers(bestSolution.players.get(i), bestSolution.players.get(opponent)));
                pairedIds.add(i);
                pairedIds.add(opponent);
            }
        }
        return r;
    }

    private void gac(VariableState previousState) {
        if(previousState == null) previousState = initialState;

        VariableIndex index = previousState.getUnassignedVariable();
        if(index == null){
            bestSolution = previousState;
            bestWeight = previousState.variables.values().stream().mapToInt(v -> v.get(0).weight()).sum();
            return;
        }

        for(VariableAssignment pos : previousState.getVar(index)){
            VariableState nextState = new VariableState(previousState);
            nextState.setVar(index, pos.opponentIndex());
            LinkedList<Constraint> constraints = getConstraintsForVar(index);
            HashSet<String> constraintNames = constraints.stream().map(Constraint::name)
                    .collect(Collectors.toCollection(HashSet::new));

            if(gacEnforce(constraints, constraintNames, nextState))
                gac(nextState);
        }
    }

    private boolean gacEnforce(LinkedList<Constraint> gacQueue, HashSet<String> constraintNames, VariableState state) {
        while (!gacQueue.isEmpty()){
            Constraint constraint = gacQueue.removeFirst();
            constraintNames.remove(constraint.name());
            Iterable<VariableIndex> modifiedVariables = constraint.applyTo(state, players);
            if(modifiedVariables == null)
                return false;
            for(VariableIndex variableIndex : modifiedVariables){
                List<Constraint> toAdd = getConstraintsForVar(variableIndex);
                toAdd.removeIf((d -> constraintNames.contains(d.name())));
                gacQueue.addAll(toAdd);
                constraintNames.addAll(toAdd.stream().map(Constraint::name).collect(Collectors.toList()));
            }
        }
        return true;
    }

    private LinkedList<Constraint> getConstraintsForVar(VariableIndex variableIndex) {
        LinkedList<Constraint> constraints = new LinkedList<>();
        constraints.add(new PlayerConstraint(variableIndex.player()));
        constraints.add(new RoundConstraint(variableIndex.round()));
        constraints.add(weightFunction.getWeightConstraint(bestWeight));
        return constraints;
    }

    private static Game pairPlayers(Player p, Player q) {
        if (p.getGamesAsBlack() >= q.getGamesAsBlack())
            return new Game(p, q);
        return new Game(q, p);
    }
}

class PlayerConstraint implements Constraint {
    //Each player does not have the same opponent more than once
    private final int playerIndex;
    private final String name;
    public PlayerConstraint(int playerIndex) {
        this.playerIndex = playerIndex;
        this.name = "h" + playerIndex;
    }

    @Override
    public Iterable<VariableIndex> applyTo(VariableState state, List<Player> players) {
        LinkedList<VariableIndex> modified = new LinkedList<>();
        HashSet<Integer> usedValues = new HashSet<>();
        for (int i = 0; i < state.roundsRemaining; i++) {
            LinkedList<VariableAssignment> w = state.getVar(playerIndex, i);
            int opponentIndex = w.get(0).opponentIndex();
            if(w.size() == 1 && opponentIndex != -1)
                usedValues.add(opponentIndex);
        }

        for (int i = 0; i < state.roundsRemaining; i++) {
            LinkedList<VariableAssignment> w = state.getVar(playerIndex, i);
            if(w.size() == 1) continue;

            if(w.removeIf(v -> usedValues.contains(v.opponentIndex()))){
                if(w.isEmpty()) return null;
                modified.add(new VariableIndex(playerIndex, i));
            }
        }
        return modified;
    }

    @Override
    public String name() {
        return name;
    }
}

class RoundConstraint implements Constraint {
    //In a round, each player plays at most one game.
    private final int roundIndex;
    private final String name;
    public RoundConstraint(int roundIndex) {
        this.roundIndex = roundIndex;
        this.name = "v" + roundIndex;
    }

    @Override
    public Iterable<VariableIndex> applyTo(VariableState state, List<Player> players) {
        LinkedList<VariableIndex> modified = new LinkedList<>();
        HashSet<Integer> usedValues = new HashSet<>();
        for (int i = 0; i < players.size(); i++) {
            LinkedList<VariableAssignment> w = state.getVar(i, roundIndex);
            int opponentIndex = w.get(0).opponentIndex();
            if(w.size() == 1 && opponentIndex != -1){
                usedValues.add(opponentIndex);
                usedValues.add(i);
                if(state.setVar(opponentIndex, roundIndex, i)){
                    if(state.getVar(opponentIndex, roundIndex).isEmpty()) return null;
                    modified.add(new VariableIndex(opponentIndex, roundIndex));
                }
            }
        }

        for (int i = 0; i < players.size(); i++) {
            LinkedList<VariableAssignment> w = state.getVar(i, roundIndex);
            if(w.size() == 1) continue;

            if(w.removeIf(v -> usedValues.contains(v.opponentIndex()))){
                if(w.isEmpty()) return null;
                modified.add(new VariableIndex(i, roundIndex));
            }
        }
        return modified;
    }

    @Override
    public String name() {
        return name;
    }
}

class VariableState {
    public HashMap<VariableIndex, LinkedList<VariableAssignment>> variables;
    WeightFunction weightFunction;
    int roundsRemaining;
    ArrayList<Player> players;
    VariableState(ArrayList<Player> players, int roundsRemaining, WeightFunction weightFunction){
        this.players = players;
        this.roundsRemaining = roundsRemaining;
        this.weightFunction = weightFunction;
        variables = new HashMap<>();
        for(int i = 0; i < players.size(); i++)
            for (int j = 0; j < roundsRemaining; j++){
                VariableIndex variableIndex = new VariableIndex(i, j);
                variables.put(variableIndex, initializeVariable(players.size(), variableIndex));
            }
    }

    private LinkedList<VariableAssignment> initializeVariable(int numPlayers, VariableIndex variableIndex) {
        LinkedList<VariableAssignment> values = new LinkedList<>();
        Player p = players.get(variableIndex.player());
        for(int opponentIndex = 0; opponentIndex < numPlayers; opponentIndex++){
            if(opponentIndex == variableIndex.player() || p.hasPlayedAgainst(players.get(opponentIndex)))
                continue;
            values.add(new VariableAssignment(opponentIndex, weightFunction.calculateWeight(opponentIndex, p, variableIndex.round(), players)));
        }
        values.addLast(new VariableAssignment(-1, weightFunction.calculateWeight(-1, p, variableIndex.round(), players)));
        values.sort(Comparator.comparing(VariableAssignment::weight));
        return values;
    }

    public VariableState(VariableState stateToCopy){
        variables = new HashMap<>();
        this.players = stateToCopy.players;
        this.roundsRemaining = stateToCopy.roundsRemaining;
        this.weightFunction = stateToCopy.weightFunction;
        stateToCopy.variables.keySet().forEach(v -> variables.put(v, new LinkedList<>(stateToCopy.variables.get(v))));
    }

    public void trivialize() {
        for(LinkedList<VariableAssignment> value : variables.values())
            if (value.size() > 1)
                value.removeIf(x -> x.opponentIndex() != -1);
    }

    public VariableIndex getUnassignedVariable() {
        int minSize = Integer.MAX_VALUE;
        VariableIndex minVariableIndex = null;
        for (int i = 0; i < players.size(); i++){
            VariableIndex variableIndex = new VariableIndex(i, 0);
            int domainSize = variables.get(variableIndex).size();
            if(1 < domainSize && domainSize < minSize) {
                minSize = domainSize;
                minVariableIndex = variableIndex;
            }
        }
        if(minVariableIndex != null)
            return minVariableIndex;

        for (int j = 1; j < roundsRemaining; j++)
            for (int i = 0; i < players.size(); i++){
                VariableIndex variableIndex = new VariableIndex(i, j);
                int domainSize = variables.get(variableIndex).size();
                if(1 < domainSize && domainSize < minSize){
                    minSize = domainSize;
                    minVariableIndex = variableIndex;
                }
            }
        return minVariableIndex;
    }

    public LinkedList<VariableAssignment> getVar(VariableIndex index) {
        return variables.get(index);
    }
    public LinkedList<VariableAssignment> getVar(int playerIndex, int roundIndex) {
        return variables.get(new VariableIndex(playerIndex, roundIndex));
    }

    public boolean setVar(VariableIndex i, int value) {
        return variables.get(i).removeIf(v -> v.opponentIndex() != value);
    }

    public int getWeightOf(VariableIndex variableIndex) {
        return variables.get(variableIndex).get(0).weight();
    }

    public int numSitOuts(int r) {
        int num = 0;
        for (int i = 0; i < players.size(); i++){
            LinkedList<VariableAssignment> domain = variables.get(new VariableIndex(i, r));
            if (domain.size() == 1 && domain.get(0).opponentIndex() == -1)
                num++;
        }
        return num;
    }

    public boolean setVar(int playerIndex, int roundIndex, int value) {
        return variables.get(new VariableIndex(playerIndex, roundIndex)).removeIf(v -> v.opponentIndex() != value);
    }
}
