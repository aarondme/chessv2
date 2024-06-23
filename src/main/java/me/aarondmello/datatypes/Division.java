package me.aarondmello.datatypes;
import java.util.*;


import me.aarondmello.driver.PairingSystem;


public class Division{

    static class PlayerComparator implements Comparator<Player>{
        Tiebreak[] tiebreaks;
        PlayerComparator(Tiebreak[] tb){
            tiebreaks = tb;
        }
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

    static class SortingPlayerComparator implements Comparator<Player>{
        Comparator<Player> comparator;
        SortingPlayerComparator(Comparator<Player> c){comparator = c;}

        @Override
        public int compare(Player o1, Player o2) {
            int r = comparator.compare(o1, o2);

            return (r == 0)? o1.getID() - o2.getID() : r;
        }
    }

    private final String name;
    private final ArrayList<Player> players = new ArrayList<>();
    private int maxID = 0;

    Tiebreak[] tiebreaks = null;
    private Round currentRound;

    Division(String name){
        this.name = name;
        setTiebreaks(null);
    }
    public String getName() {
        return name;
    }

    public Comparator<Player> getPlayerComparator(){
        return new PlayerComparator(tiebreaks);
    }
    public void sortPlayers(){
        players.sort((new SortingPlayerComparator(getPlayerComparator())).reversed());
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
        for(Player p : players)
            p.clearTiebreaks();
        for(Tiebreak t: tiebreaks)
            t.computeTiebreak(players, getPlayerComparator());
        sortPlayers();
    }
    public void pairRound(int roundNumber, int totalRounds, boolean isRegional) {
        sortPlayers();
        ArrayList<Player> activePlayers = new ArrayList<>(players);
        ArrayList<Player> inactivePlayers = new ArrayList<>(players);
        activePlayers.removeIf(p -> !p.isActive());
        inactivePlayers.removeIf(Player::isActive);

        currentRound = PairingSystem.pairRound(roundNumber, activePlayers, totalRounds,
                PairingSystem.getWeightFunction(isRegional, activePlayers, roundNumber, totalRounds));

        for (Player p : inactivePlayers) {
            Game g = new Game(p, NullPlayer.getInstance());
            g.setResult(GameResult.BLACK_WIN);
            currentRound.addGame(g);
        }
    }

    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
    }
    public void confirmRoundResults() {
        for(Game game : currentRound.getGames())
            game.confirmResult();
        initialize();
    }


    public boolean validateRoundResults() {
        return currentRound.getGames().stream().allMatch(g -> g.result != null);
    }
    public void setGameResultByID(int id, GameResult result) {
        currentRound.setResultByID(id, result);
    }
    public LinkedList<Game> getPairing() {
        return currentRound.getGames();
    }

    public Tiebreak[] getTiebreaks() {
        return tiebreaks;
    }


    public void setTiebreaks(TiebreakType[] tiebreakTypes){
        TiebreakType[] defaultTiebreaks = {TiebreakType.BuchholzCutOne, TiebreakType.Buchholz,
                TiebreakType.SonnebornBerger, TiebreakType.ProgressiveScores,
                TiebreakType.DirectEncounter, TiebreakType.WinCount,
                TiebreakType.WinCountAsBlack};

        if(tiebreakTypes == null)
            tiebreakTypes = defaultTiebreaks;

        Tiebreak[] tbs = new Tiebreak[tiebreakTypes.length];
        for(int i = 0; i < tiebreakTypes.length; i++){
            switch (tiebreakTypes[i]){
                case Buchholz -> tbs[i] = new SimpleTiebreak(new Buchholz());
                case BuchholzCutOne -> tbs[i] = new SimpleTiebreak(new BuchholzCutOne());
                case DirectEncounter -> tbs[i] = new DirectEncounter();
                case SonnebornBerger -> tbs[i] = new SimpleTiebreak(new SonnebornBerger());
                case ProgressiveScores -> tbs[i] = new SimpleTiebreak(new ProgressiveScores());
                case WinCount -> tbs[i] = new SimpleTiebreak(new WinCount());
                case WinCountAsBlack -> tbs[i] = new SimpleTiebreak(new WinCountAsBlack());
            }
        }
        tiebreaks = tbs;
    }

}