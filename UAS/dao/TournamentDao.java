package dao;

import model.Tournament;
import util.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentDao {
    public List<Tournament> getAll() {
        List<Tournament> list = new ArrayList<>();
        String sql = "SELECT * FROM tournaments ORDER BY id DESC"; // Urutkan dari yang terbaru
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(new Tournament(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}