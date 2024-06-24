package me.aarondmello.datatypes;
import java.util.*;
import java.util.stream.Collectors;


import me.aarondmello.driver.PairingSystem;


public class Division{

    class ScoreComparator implements Comparator<Player>{
        @Override
        public int compare(Player o1, Player o2) {
            if(o1.getScore() != o2.getScore()) return o1.getScore() - o2.getScore();
            for(Tiebreak t : tiebreaks){
                if(o1.getTiebreakScore(t.type()) != o2.getTiebreakScore(t.type()))
                    return o1.getTiebreakScore(t.type()) - o2.getTiebreakScore(t.type());
            }
            return 0;
        }
    }

    class SortingPlayerComparator implements Comparator<Player>{
        @Override
        public int compare(Player o1, Player o2) {
            int r = scoreComparator.compare(o1, o2);

            return (r == 0)? o1.getID() - o2.getID() : r;
        }
    }

    private final String name;
    private final ArrayList<Player> players = new ArrayList<>();
    private int maxID = 0;
    private final ScoreComparator scoreComparator = new ScoreComparator();
    private final SortingPlayerComparator sortingPlayerComparator = new SortingPlayerComparator();

    private Tiebreak[] tiebreaks = Tiebreak.getDefaultTiebreaks();
    private LinkedList<Game> currentRound;

    Division(String name){
        this.name = name;
        setTiebreaks(null);
    }
    public String getName() {
        return name;
    }

    public void sortPlayers(){
        players.sort((sortingPlayerComparator).reversed());
    }
    public void addPlayer(Player p, boolean maintainIds){
        if(!maintainIds)
            p.setID(maxID++);
        players.add(p);
    }
    public void addPlayer(Player p){
        addPlayer(p, false);
    }
    public void addPlayers(Iterable<Player> toAdd){
        addPlayers(toAdd, false);
    }
    public void addPlayers(Iterable<Player> toAdd, boolean maintainIds){
        for (Player p: toAdd) {
            addPlayer(p, maintainIds);
        }
    }
    public List<Player> getPlayers(){
        return players;
    }
    public Player getPlayerById(int id){
        for(Player p : players){
            if(p.getID() == id)
                return p;
        }
        return null;
    }
    public void removePlayer(int playerID) {
        players.removeIf(player -> player.getID() == playerID);
    }

    public void randomizeIds(){
        Collections.shuffle(players);
        for(int i = 0; i < players.size(); i++){
            Player player = players.get(i);
            player.setID(i);
        }
    }
    public void initialize() {
        for(Player p : players){
            p.clearTiebreaks();
            p.computeScore();
        }
        for(Tiebreak t: tiebreaks){
            if(t instanceof DirectEncounter)
                computePlayerRanks();
            t.computeTiebreak(players);
        }

        computePlayerRanks();
    }

    private void computePlayerRanks() {
        sortPlayers();
        int rank = 1;
        int tiedWith = 1;
        for(Player player : players) {
            if (scoreComparator.compare(player, players.get(tiedWith - 1)) != 0)
                tiedWith = rank;
            player.setRank(tiedWith);
            rank++;
        }
    }

    public void pairRound(int roundNumber, int totalRounds, boolean isRegional) {
        sortPlayers();
        ArrayList<Player> activePlayers = players.stream().filter(Player::isActive).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Player> inactivePlayers = players.stream().filter(p -> !p.isActive()).collect(Collectors.toCollection(ArrayList::new));

        currentRound = PairingSystem.pairRound(roundNumber, activePlayers, inactivePlayers, totalRounds,
                PairingSystem.getWeightFunction(isRegional, activePlayers, roundNumber, totalRounds));
    }

    public void setCurrentRound(LinkedList<Game> currentRound) {
        this.currentRound = currentRound;
    }
    public void confirmRoundResults() {
        for(Game game : currentRound)
            game.confirmResult();
        currentRound = null;
        initialize();
    }


    public boolean validateRoundResults() {
        return currentRound.stream().allMatch(g -> g.result != null);
    }
    public void setGameResultByID(int id, GameResult result) {
        if(0 <= id && id < currentRound.size())
            currentRound.get(id).setResult(result);
    }
    public LinkedList<Game> getPairing() {
        return currentRound;
    }

    public Tiebreak[] getTiebreaks() {
        return tiebreaks;
    }


    public void setTiebreaks(TiebreakType[] tiebreakTypes){
        if(tiebreakTypes == null){
            tiebreaks = Tiebreak.getDefaultTiebreaks();
            return;
        }

        Tiebreak[] tbs = new Tiebreak[tiebreakTypes.length];
        for(int i = 0; i < tiebreakTypes.length; i++){
            tbs[i] = Tiebreak.fromTiebreakType(tiebreakTypes[i]);

        }
        tiebreaks = tbs;
    }


}