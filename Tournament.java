import java.io.File;
import java.util.*;
public class Tournament{
    private String name;
    private LinkedList<Division> divisions;
    private PlayerComparator playerComparator;
    private int currentRound, numberOfRounds;
    private File toSaveTo;

    Tournament(String name, int numberOfRounds, File toSaveTo){
        this.name = name;
        this.numberOfRounds = numberOfRounds;
        this.toSaveTo = toSaveTo;
    }

    public void createDivision(String name){
       //TODO
    }

    public void startNextRound(){
        //TODO
    }

    public String getTournamentName() {
        return name;
    }
    public LinkedList<Division> getDivisions() {
        return divisions;
    }
    public int getCurrentRound() {
        return currentRound;
    }
    public int getNumberOfRounds() {
        return numberOfRounds;
    }
}