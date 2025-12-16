package dao;

import model.Tournament;
import util.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentDao implements DaoInterface<Tournament> {

    @Override
    public List<Tournament> getAll() {
        List<Tournament> list = new ArrayList<>();
        String sql = "SELECT * FROM tournaments ORDER BY id DESC"; // Yang terbaru paling atas
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Tournament t = new Tournament(
                    rs.getInt("id"),
                    rs.getInt("sport_id"),
                    rs.getString("name"),
                    rs.getDate("start_date"),
                    rs.getString("status")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- METHOD YANG SEBELUMNYA HILANG ---
    @Override
    public Tournament get(int id) {
        Tournament t = null;
        String sql = "SELECT * FROM tournaments WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                t = new Tournament(
                    rs.getInt("id"),
                    rs.getInt("sport_id"),
                    rs.getString("name"),
                    rs.getDate("start_date"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    // --- METHOD ADD (Untuk Turnamen Baru) ---
    @Override
    public boolean add(Tournament t) {
        // ID auto increment, jadi tidak perlu di-insert
        // start_date di Java Date perlu convert ke SQL Date
        String sql = "INSERT INTO tournaments (sport_id, name, start_date, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, t.getSportId());
            stmt.setString(2, t.getName());
            // Konversi java.util.Date ke java.sql.Date
            stmt.setDate(3, new java.sql.Date(t.getStartDate().getTime()));
            stmt.setString(4, t.getStatus()); // Biasanya "OPEN" atau "ONGOING"
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Implementasi kosong untuk Update/Delete jika belum dipakai ---
    @Override
    public boolean update(Tournament t) {
        // Implementasi update jika diperlukan nanti
        return false;
    }

    @Override
    public boolean delete(int id) {
        // Implementasi delete jika diperlukan nanti
        return false;
    }
}