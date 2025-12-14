package model; // Sesuaikan dengan package Anda (misal: com.sporta.model)

public class Player extends BaseModel {
    private int teamId;
    private String name;
    private int jerseyNumber;
    private String position;

    public Player() {
        super(0);
    }

    public Player(int id, int teamId, String name, int jerseyNumber, String position) {
        super(id); // Set ID ke BaseModel
        this.teamId = teamId;
        this.name = name;
        this.jerseyNumber = jerseyNumber;
        this.position = position;
    }

    public Player(int teamId, String name, int jerseyNumber, String position) {
        super(0);
        this.teamId = teamId;
        this.name = name;
        this.jerseyNumber = jerseyNumber;
        this.position = position;
    }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(int jerseyNumber) { this.jerseyNumber = jerseyNumber; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    @Override
    public String toString() {
        return name + " (#" + jerseyNumber + ")";
    }
}