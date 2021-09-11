import java.util.LinkedList;

public class Player{
    private String name;
    private String organization;
    private int score;
    private int gamesAsWhite, gamesAsBlack;
    private LinkedList<MatchResult> matchResults;
    private Tiebreaks tiebreaks;

    Player(String name, String organization){
        this.name = name;
        this.organization = organization;
        this.score = 0;
        this.matchResults = new LinkedList<MatchResult>();
        this.tiebreaks = new Tiebreaks();
        this.gamesAsBlack = 0;
        this.gamesAsWhite = 0;
    }
    public String getName() {
        return name;
    }
    public String getOrganization() {
        return organization;
    }
    public int getScore() {
        return score;
    }
    public LinkedList<MatchResult> getMatchResults() {
        return matchResults;
    }
    public Tiebreaks getTiebreaks() {
        return tiebreaks;
    }
    public int getGamesAsBlack() {
        return gamesAsBlack;
    }
    public int getGamesAsWhite() {
        return gamesAsWhite;
    }
    public String getDisplayName() {
        if (organization.trim().length() == 0)
          return name;
        return (name + " (" + organization + ")");
      }
    public void setName(String name) {
        this.name = name;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void addMatchResults(MatchResult matchResult){
        matchResults.add(matchResult);
        score += matchResult.getScore();
    }
    public void updateTiebreaks(LinkedList<MatchResult> matchResults){
        tiebreaks.updateTiebreaks(matchResults);
    }
}