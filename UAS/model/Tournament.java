package model;

import java.sql.Date;

public class Tournament extends BaseModel {
    private int sportId;
    private String name;
    private Date startDate;
    private String status;

    // Constructor Lengkap (Saat ambil dari DB)
    public Tournament(int id, int sportId, String name, Date startDate, String status) {
        super(id);
        this.sportId = sportId;
        this.name = name;
        this.startDate = startDate;
        this.status = status;
    }
    
    // Constructor untuk Insert Baru (Tanpa ID)
    public Tournament(int sportId, String name, Date startDate, String status) {
        super(0); // ID 0 sementara
        this.sportId = sportId;
        this.name = name;
        this.startDate = startDate;
        this.status = status;
    }

    // --- GETTER & SETTER (YANG TADINYA HILANG) ---
    
    public int getSportId() { return sportId; }
    public void setSportId(int sportId) { this.sportId = sportId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getStartDate() { return startDate; } // <--- INI YANG DICARI ERRORNYA
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Override toString agar ComboBox menampilkan Nama
    @Override
    public String toString() {
        return name + " (" + status + ")";
    }
}