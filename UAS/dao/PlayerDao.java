package dao;

import model.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import helper.DatabaseHelper;

// DAO untuk mengelola operasi database terkait Player menggunakan PreparedStatement dan try-with-resources
public class PlayerDao {
	// Mengambil semua player dari team tertentu menggunakan PreparedStatement
	public List<Player> getPlayersByTeam(int teamId) {
        List<Player> list = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE team_id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teamId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Player p = new Player(
                        rs.getInt("id"),
                        rs.getInt("team_id"),
                        rs.getString("name"),
                        rs.getInt("jersey_number"),
                        rs.getString("position")
                    );
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
	
	// Menambahkan player baru ke database
	public boolean addPlayer(Player player) {
        String sql = "INSERT INTO players (team_id, name, jersey_number, position) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, player.getTeamId());
            stmt.setString(2, player.getName());
            stmt.setInt(3, player.getJerseyNumber());
            stmt.setString(4, player.getPosition());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    // Mengupdate data player di database
    public boolean updatePlayer(Player player) {
        String sql = "UPDATE players SET name = ?, jersey_number = ?, position = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getName());
            stmt.setInt(2, player.getJerseyNumber());
            stmt.setString(3, player.getPosition());
            stmt.setInt(4, player.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    // Menghapus player dari database berdasarkan ID
    public boolean deletePlayer(int id) {
        String sql = "DELETE FROM players WHERE id = ?";
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
