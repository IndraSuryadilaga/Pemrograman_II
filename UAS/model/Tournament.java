package model;

import java.sql.Date;

public class Tournament extends BaseModel {
    private int sportId;
    private String name;
    private Date startDate;
    private String status;

    public Tournament(int id, int sportId, String name, Date startDate, String status) {
        super(id);
        this.sportId = sportId;
        this.name = name;
        this.startDate = startDate;
        this.status = status;
    }

    public String getName() { return name; }
    public String getStatus() { return status; }
    public int getSportId() { return sportId; }

    // Override toString agar ComboBox menampilkan Nama, bukan alamat memori
    @Override
    public String toString() {
        return name + " (" + status + ")";
    }
}