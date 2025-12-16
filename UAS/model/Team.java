package model;

// Team mewarisi BaseModel dengan field private (Inheritance dan Encapsulation)
public class Team extends BaseModel {
    private String name;
    private String logoPath;

    // Constructor untuk membuat Team baru tanpa ID (Constructor Overloading)
    public Team(String name, String logoPath) {
        super();
        this.name = name;
        this.logoPath = logoPath;
    }

    // Constructor untuk membuat Team dari data database dengan ID (Constructor Overloading)
    public Team(int id, String name, String logoPath) {
        super(id);
        this.name = name;
        this.logoPath = logoPath;
    }

    // Override toString() untuk menampilkan nama team (Polymorphism - Method Override)
    @Override
    public String toString() {
        return name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
}
