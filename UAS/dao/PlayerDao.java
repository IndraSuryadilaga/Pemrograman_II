package dao;

import model.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DatabaseHelper;

public class PlayerDao {
	public List<Player> getPlayersByTeam(int teamId) {
        List<Player> list = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE team_id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teamId);
            ResultSet rs = stmt.executeQuery();
            
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
