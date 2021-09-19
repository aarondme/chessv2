import java.io.File;
import java.util.*;
public class Tournament{
    private String name;
    private LinkedList<Division> divisions;
    private PlayerComparator playerComparator;
    private int roundNumber, totalRounds;
    private File toSaveTo;

    Tournament(String name, int totalRounds, File toSaveTo){
        this.name = name;
        this.totalRounds = totalRounds;
        this.toSaveTo = toSaveTo;
        this.roundNumber = 1;
    }

    public void createDivision(String name){
       //TODO
    }
    public void runNextRound(GUI gui){
        startNextRound();
    }

    private void startNextRound(){
        for(Division d : divisions){
            d.sortPlayers(playerComparator);
            d.createRound(roundNumber, totalRounds);
        }
    }

    public String getTournamentName() {
        return name;
    }
    public LinkedList<Division> getDivisions() {
        return divisions;
    }
    public int getRoundNumber() {
        return roundNumber;
    }
    public int getTotalRounds() {
        return totalRounds;
    }
    public boolean hasRoundsRemaining(){
        return (roundNumber < totalRounds);
    }
}