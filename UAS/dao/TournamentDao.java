package dao;

import model.Tournament;
import util.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentDao {

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
}