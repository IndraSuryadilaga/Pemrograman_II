package model;

public class Match extends BaseModel {
    private int tournamentId;
    private int homeTeamId;
    private int awayTeamId;
    private int roundNumber;
    private int bracketIndex;
    private boolean isFinished;
    private int homeScore;
    private int awayScore;
    private String homeTeamName;
    private String awayTeamName;
    private String tournamentName;
    private String matchDate;
    private int currentQuarter = 1;
    private int remainingSeconds = 600;
    private boolean isTimerRunning = false;

    public Match() { super(0); }

    public Match(int id, int tournamentId, String tournamentName, int homeTeamId, String homeTeamName, 
            int awayTeamId, String awayTeamName, int roundNumber, int bracketIndex, 
            boolean isFinished, int homeScore, int awayScore, String matchDate) {
	   super(id);
	   this.tournamentId = tournamentId;
	   this.tournamentName = tournamentName;
	   this.homeTeamId = homeTeamId;
	   this.homeTeamName = homeTeamName;
	   this.awayTeamId = awayTeamId;
	   this.awayTeamName = awayTeamName;
	   this.roundNumber = roundNumber;
	   this.bracketIndex = bracketIndex;
	   this.isFinished = isFinished;
	   this.homeScore = homeScore;
	   this.awayScore = awayScore;
	   this.matchDate = matchDate;
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
    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }
    
    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }
    
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }

    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }
    
    public int getCurrentQuarter() { return currentQuarter; }
    public void setCurrentQuarter(int currentQuarter) { this.currentQuarter = currentQuarter; }

    public int getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(int remainingSeconds) { this.remainingSeconds = remainingSeconds; }

    public boolean isTimerRunning() { return isTimerRunning; }
    public void setTimerRunning(boolean timerRunning) { isTimerRunning = timerRunning; }
    
    @Override
    public String toString() {
        return homeTeamName + " vs " + awayTeamName;
    }
}