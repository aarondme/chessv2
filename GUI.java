import java.util.HashMap;

public interface GUI{
    public void displayWelcomeScreen();
    public boolean getIfNewTournament();
    public Tournament getNewTournament();
    public Tournament getExistingTournament();
    public HashMap<String,int[]> getRoundResults(Tournament tournament);
    public void displayResults(Tournament tournament);
    public boolean getIfSavingResults();
    public boolean wasCancelPressed();
}