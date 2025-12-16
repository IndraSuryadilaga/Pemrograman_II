package model;

import java.sql.Date;

// Tournament mewarisi BaseModel dengan field private (Inheritance dan Encapsulation)
public class Tournament extends BaseModel {
    private int sportId;
    private String name;
    private Date startDate;
    private String status;

    // Constructor lengkap untuk membuat Tournament dari data database
    public Tournament(int id, int sportId, String name, Date startDate, String status) {
        super(id);
        this.sportId = sportId;
        this.name = name;
        this.startDate = startDate;
        this.status = status;
    }
    
    // Constructor untuk membuat Tournament baru tanpa ID (Constructor Overloading)
    public Tournament(int sportId, String name, Date startDate, String status) {
        super(0);
        this.sportId = sportId;
        this.name = name;
        this.startDate = startDate;
        this.status = status;
    }

    // Override toString() untuk menampilkan nama dan status di ComboBox (Polymorphism - Method Override)
    @Override
    public String toString() {
        return name + " (" + status + ")";
    }
    public int getSportId() { return sportId; }
    public void setSportId(int sportId) { this.sportId = sportId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}