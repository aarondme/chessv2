package me.aarondmello.datatypes;
import java.util.*;

import me.aarondmello.driver.PairingSystem;

public class Division{
    public void setTotalRounds(int rounds) {
        totalRounds = rounds;
    }

    class PlayerComparator implements Comparator<Player>{
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

    private String name;
    private ArrayList<Player> players = new ArrayList<>();
    private int totalRounds;
    private int maxID = 0;
    private PairingSystem pairingSystem = new PairingSystem();

    Tiebreak[] tiebreaks = null;
    private Round currentRound;

    Division(String name){
        this.name = name;
        setTiebreaks(null);
    }
    public String getName() {
        return name;
    }

    private Comparator<Player> getPlayerComparator(){
        return new PlayerComparator(tiebreaks);
    }
    public void sortPlayers(){
        players.sort(getPlayerComparator().reversed());
    }
    public void addPlayer(Player p){
        p.setID(maxID++);
        players.add(p);
    }
    public void addPlayers(List<Player> toAdd){
        for (Player p: toAdd) {
            addPlayer(p);
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
        ArrayList<Integer> ids = new ArrayList<>();
        for(int i = 0; i < players.size(); i++)
            ids.add(i);
        Collections.shuffle(ids);
        for(int i = 0; i < players.size(); i++){
            Player player = players.get(i);
            player.setID(ids.get(i));
        }
    }
    public void initialize() {
        for(Player p : players)
            p.clearTiebreaks();
        for(Tiebreak t: tiebreaks)
            t.computeTiebreak(players, getPlayerComparator());
        sortPlayers();
    }
    public void pairRound(int roundNumber) {
        currentRound = pairingSystem.pairRound(roundNumber, players, totalRounds);
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
        for(Game game : currentRound.getGames()){
            if(!game.isResultValid())
                return false;
        }
        return true;
    }
    public void setGameResultByID(int id, int result) {
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