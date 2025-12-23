package dao;

import model.Tournament;
import helper.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO untuk mengelola operasi database terkait Tournament menggunakan PreparedStatement dan try-with-resources
public class TournamentDao implements DaoInterface<Tournament> {

    // Mengambil semua tournament dari database diurutkan dari yang terbaru
    @Override
    public List<Tournament> getAll() {
        List<Tournament> list = new ArrayList<>();
        String sql = "SELECT * FROM tournaments ORDER BY id DESC";
        
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

    // Mengambil satu tournament berdasarkan ID menggunakan PreparedStatement
    @Override
    public Tournament get(int id) {
        Tournament t = null;
        String sql = "SELECT * FROM tournaments WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    t = new Tournament(
                        rs.getInt("id"),
                        rs.getInt("sport_id"),
                        rs.getString("name"),
                        rs.getDate("start_date"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    // Menambahkan tournament baru ke database dengan konversi java.util.Date ke java.sql.Date
    @Override
    public boolean add(Tournament t) {
        String sql = "INSERT INTO tournaments (sport_id, name, start_date, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, t.getSportId());
            stmt.setString(2, t.getName());
            // Konversi java.util.Date ke java.sql.Date untuk kompatibilitas database
            stmt.setDate(3, new java.sql.Date(t.getStartDate().getTime()));
            stmt.setString(4, t.getStatus());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Membuat turnamen baru dan mengembalikan ID-nya
    public int createTournament(String name, int sportId) {
        String sql = "INSERT INTO tournaments (sport_id, name, start_date, status) VALUES (?, ?, datetime('now', 'localtime'), 'ONGOING')";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, sportId);
            stmt.setString(2, name);
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Update tournament
    @Override
    public boolean update(Tournament t) {
        return false;
    }

    // Delete tournament
    @Override
    public boolean delete(int id) {
        return false;
    }
}