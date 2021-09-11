
public interface GUI{
    public void displayWelcomeScreen();
    public boolean getIfNewTournament();
    public Tournament getNewTournament();
    public Tournament getExistingTournament();
    public void getRoundResults(Round round);
    public void displayResults(Tournament tournament);
    public boolean getIfSavingResults();
    public boolean wasCancelPressed();
}