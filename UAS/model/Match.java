package model;

public class Match extends BaseModel {
    private int tournamentId;
    private int homeTeamId;
    private int awayTeamId;
    private String homeTeamName;
    private String awayTeamName;
    
    private int roundNumber;
    private int bracketIndex;
    private boolean isFinished;
    private int homeScore;
    private int awayScore;

    public Match() { super(0); }

    public Match(int id, int tournamentId, int homeTeamId, String homeName, 
                 int awayTeamId, String awayName, int round, int idx, 
                 boolean finished, int hScore, int aScore) {
        super(id);
        this.tournamentId = tournamentId;
        this.homeTeamId = homeTeamId;
        this.homeTeamName = homeName;
        this.awayTeamId = awayTeamId;
        this.awayTeamName = awayName;
        this.roundNumber = round;
        this.bracketIndex = idx;
        this.isFinished = finished;
        this.homeScore = hScore;
        this.awayScore = aScore;
    }
    
    public int getTournamentId() { return tournamentId; }
    public int getHomeTeamId() { return homeTeamId; }
    public int getAwayTeamId() { return awayTeamId; }
    public String getHomeTeamName() { return homeTeamName; } 
    public String getAwayTeamName() { return awayTeamName; } 
    public int getRoundNumber() { return roundNumber; }
    public int getBracketIndex() { return bracketIndex; }
    public boolean isFinished() { return isFinished; }
    public int getHomeScore() { return homeScore; }
    public int getAwayScore() { return awayScore; }
    
    @Override
    public String toString() {
        return homeTeamName + " vs " + awayTeamName;
    }
}