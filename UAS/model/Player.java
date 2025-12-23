package model;

// Player mewarisi BaseModel dengan field private (Inheritance dan Encapsulation)
public class Player extends BaseModel {
    private int teamId;
    private int jerseyNumber;
    private int matchPoints;
    private int matchFouls;
    private String name;
    private String position;

    // Constructor untuk membuat Player dari data database dengan ID
    public Player(int id, int teamId, String name, int jerseyNumber, String position) {
        super(id);
        this.teamId = teamId;
        this.name = name;
        this.jerseyNumber = jerseyNumber;
        this.position = position;
    }

    // Constructor untuk membuat Player baru tanpa ID (Constructor Overloading)
    public Player(int teamId, String name, int jerseyNumber, String position) {
        super(0);
        this.teamId = teamId;
        this.name = name;
        this.jerseyNumber = jerseyNumber;
        this.position = position;
    }
    
    // Override toString() untuk menampilkan nama dan nomor jersey (Polymorphism - Method Override)
    @Override
    public String toString() {
        return name + " (#" + jerseyNumber + ")";
    }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(int jerseyNumber) { this.jerseyNumber = jerseyNumber; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public int getMatchPoints() { return matchPoints; }
    public void setMatchPoints(int matchPoints) { this.matchPoints = matchPoints; }

    public int getMatchFouls() { return matchFouls; }
    public void setMatchFouls(int matchFouls) { this.matchFouls = matchFouls; }
}