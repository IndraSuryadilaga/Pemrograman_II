package dao;

import model.Match;
import util.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchDao {
    
    // Mengambil Data Match beserta Nama Timnya
    public List<Match> getMatchesByTournament(int tournamentId) {
        List<Match> list = new ArrayList<>();
        // Alias th = Team Home, ta = Team Away
        String sql = "SELECT m.*, th.name as home_name, ta.name as away_name " +
                     "FROM matches m " +
                     "LEFT JOIN teams th ON m.home_team_id = th.id " +
                     "LEFT JOIN teams ta ON m.away_team_id = ta.id " +
                     "WHERE m.tournament_id = ? " +
                     "ORDER BY m.round_number DESC, m.bracket_index ASC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tournamentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Match m = new Match(
                    rs.getInt("id"),
                    rs.getInt("tournament_id"),
                    rs.getInt("home_team_id"),
                    rs.getString("home_name"),
                    rs.getInt("away_team_id"),
                    rs.getString("away_name"),
                    rs.getInt("round_number"),
                    rs.getInt("bracket_index"),
                    rs.getBoolean("is_finished"),
                    rs.getInt("home_score"),
                    rs.getInt("away_score")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Algoritma Generate Bracket
    public boolean generateBracket(int tournamentId, List<Integer> teamIds) {
        if (teamIds.size() < 2) return false;

        Collections.shuffle(teamIds);

        String sql = "INSERT INTO matches (tournament_id, home_team_id, away_team_id, round_number, bracket_index, match_date) " +
                     "VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Menentukan Jumlah Ronde (4 Tim = Semifinal/Round 2, 8 Tim = Quarter/Round 3)
            // Logika sederhana Jika 4 tim, 2 match. Jika 8 tim, 4 match.
            int round = (teamIds.size() == 4) ? 2 : 4; 
            
            int bracketIndex = 1;
            
            // Pairing/Menjodohkan Setiap 2 Tim
            for (int i = 0; i < teamIds.size(); i += 2) {
                int homeId = teamIds.get(i);
                int awayId = teamIds.get(i + 1);

                stmt.setInt(1, tournamentId);
                stmt.setInt(2, homeId);
                stmt.setInt(3, awayId);
                stmt.setInt(4, round);
                stmt.setInt(5, bracketIndex++);
                
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update skor akhir pertandingan
    public boolean updateScore(int matchId, int homeScore, int awayScore) {
        String sql = "UPDATE matches SET home_score = ?, away_score = ?, is_finished = 1 WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, homeScore);
            stmt.setInt(2, awayScore);
            stmt.setInt(3, matchId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
