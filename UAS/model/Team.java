package model;

public class Team extends BaseModel {
    private String name;
    private String logoPath;

    // Polymorphism: Constructor Overloading 1 (Untuk Data Baru - ID 0)
    public Team(String name, String logoPath) {
        super(); // Panggil constructor parent (BaseModel)
        this.name = name;
        this.logoPath = logoPath;
    }

    // Polymorphism: Constructor Overloading 2 (Untuk Data dari Database - Ada ID)
    public Team(int id, String name, String logoPath) {
        super(id); // Set ID ke BaseModel
        this.name = name;
        this.logoPath = logoPath;
    }

    // Getter & Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    @Override
    public String toString() {
        return name; // Agar saat masuk ComboBox nanti yang muncul namanya, bukan alamat memori
    }
}
