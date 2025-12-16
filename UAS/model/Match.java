package model;

// Match mewarisi BaseModel dengan field private (Inheritance dan Encapsulation)
public class Match extends BaseModel {
    // Field private untuk melindungi data (Encapsulation)
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
    
    // State waktu untuk fitur Auto-Save Time
    private int currentQuarter = 1;
    private int remainingSeconds = 600;
    private boolean isTimerRunning = false;

    // Constructor default untuk membuat Match baru
    public Match() { super(0); }

    // Constructor lengkap untuk membuat Match dari data database
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
    
    // Constructor alternatif untuk membuat Match tanpa tournament name (Constructor Overloading)
    public Match(int id, int tournamentId, int homeTeamId, int awayTeamId, 
            String homeTeamName, String awayTeamName, java.util.Date matchDate, 
            int homeScore, int awayScore, int roundNumber, boolean isFinished) {
	   super(id);
	   this.tournamentId = tournamentId;
	   this.homeTeamId = homeTeamId;
	   this.awayTeamId = awayTeamId;
	   this.homeTeamName = homeTeamName;
	   this.awayTeamName = awayTeamName;
	   this.matchDate = (matchDate != null) ? matchDate.toString() : "";
	   this.homeScore = homeScore;
	   this.awayScore = awayScore;
	   this.roundNumber = roundNumber;
	   this.isFinished = isFinished;
	   this.bracketIndex = 0; 
	   this.tournamentName = ""; 
    }

    // Override method toString() dari BaseModel (Polymorphism - Method Override)
    @Override
    public String toString() {
        return homeTeamName + " vs " + awayTeamName;
    }

    public int getTournamentId() { return tournamentId; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public int getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(int homeTeamId) { this.homeTeamId = homeTeamId; }

    public String getHomeTeamName() { return homeTeamName; }
    public void setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }

    public int getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(int awayTeamId) { this.awayTeamId = awayTeamId; }

    public String getAwayTeamName() { return awayTeamName; }
    public void setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }

    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }

    public int getBracketIndex() { return bracketIndex; }
    public void setBracketIndex(int bracketIndex) { this.bracketIndex = bracketIndex; }

    public boolean isFinished() { return isFinished; }
    public void setFinished(boolean finished) { isFinished = finished; }

    public int getHomeScore() { return homeScore; }
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }

    public int getAwayScore() { return awayScore; }
    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }

    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }

    public int getCurrentQuarter() { return currentQuarter; }
    public void setCurrentQuarter(int currentQuarter) { this.currentQuarter = currentQuarter; }

    public int getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(int remainingSeconds) { this.remainingSeconds = remainingSeconds; }

    public boolean isTimerRunning() { return isTimerRunning; }
    public void setTimerRunning(boolean timerRunning) { isTimerRunning = timerRunning; }
}