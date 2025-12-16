package dao;

import model.Team;
import util.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO untuk mengelola operasi database terkait Team menggunakan PreparedStatement dan try-with-resources
public class TeamDao implements DaoInterface<Team> {

    // Mengambil semua team dari database
    @Override
    public List<Team> getAll() {
        List<Team> list = new ArrayList<>();
        String sql = "SELECT * FROM teams";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Team t = new Team(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("logo_path")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Mengambil satu team berdasarkan ID menggunakan PreparedStatement
    @Override
    public Team get(int id) {
        Team t = null;
        String sql = "SELECT * FROM teams WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    t = new Team(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("logo_path")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    // Menambahkan team baru ke database
    @Override
    public boolean add(Team team) {
    	String sql = "INSERT INTO teams (name, logo_path) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, team.getName());
            stmt.setString(2, team.getLogoPath());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mengupdate nama team di database
    @Override
    public boolean update(Team team) {
    	String sql = "UPDATE teams SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, team.getName());
            stmt.setInt(2, team.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Menghapus team dari database berdasarkan ID
    @Override
    public boolean delete(int id) {
    	String sql = "DELETE FROM teams WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
