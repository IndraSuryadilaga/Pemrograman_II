package dao;

import model.Match;
import model.Player;
import helper.DatabaseHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DAO untuk mengelola operasi database terkait Match menggunakan PreparedStatement dan try-with-resources
public class MatchDao {
    
    // Mengambil semua match dalam turnamen beserta nama tim menggunakan LEFT JOIN
    public List<Match> getMatchesByTournament(int tournamentId) {
        List<Match> matches = new ArrayList<>();
        
        String sql = "SELECT m.*, " +
                     "t1.name as home_name, t2.name as away_name " +
                     "FROM matches m " +
                     "LEFT JOIN teams t1 ON m.home_team_id = t1.id " +
                     "LEFT JOIN teams t2 ON m.away_team_id = t2.id " +
                     "WHERE m.tournament_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tournamentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Match m = new Match(
                        rs.getInt("id"),
                        rs.getInt("tournament_id"),
                        rs.getInt("home_team_id"),
                        rs.getInt("away_team_id"),
                        rs.getString("home_name"),
                        rs.getString("away_name"),
                        rs.getDate("match_date"),
                        rs.getInt("home_score"),
                        rs.getInt("away_score"),
                        rs.getInt("round_number"),
                        rs.getBoolean("is_finished")
                    );
                    
                    m.setBracketIndex(rs.getInt("bracket_index"));

                    int dbQuarter = rs.getInt("current_quarter");
                    int dbTime = rs.getInt("remaining_seconds");
                    
                    if (dbQuarter < 1) dbQuarter = 1;
                    
                    m.setCurrentQuarter(dbQuarter);
                    m.setRemainingSeconds(dbTime);
                    
                    matches.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }
	
    // Mengupdate skor akhir pertandingan dan menandai match sebagai selesai
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
    
    // Mencatat event dalam pertandingan (score, foul) ke tabel match_events untuk history tracking dan breakdown skor
    public void addMatchEvent(int matchId, int playerId, String eventType, int eventValue, int quarter) {
        String sql = "INSERT INTO match_events (match_id, player_id, event_type, event_value, timestamp, quarter) VALUES (?, ?, ?, ?, NOW(), ?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, matchId);
            stmt.setInt(2, playerId);
            stmt.setString(3, eventType);
            stmt.setInt(4, eventValue);
            stmt.setInt(5, quarter);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menghitung jumlah foul pemain dalam match menggunakan COUNT aggregate function
    public int getPlayerFoulCount(int matchId, int playerId) {
        String sql = "SELECT COUNT(*) FROM match_events WHERE match_id = ? AND player_id = ? AND event_type = 'FOUL'";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, matchId);
            stmt.setInt(2, playerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Generate struktur bracket turnamen menggunakan batch operation dan transaction management
    public boolean generateBracket(int tournamentId, List<Integer> teamIds) {
        int teamCount = teamIds.size();
        if (teamCount < 2) return false;

        // Menghitung ukuran bracket (harus pangkat 2: 4, 8, 16, 32...)
        int bracketSize = 1;
        while (bracketSize < teamCount) {
            bracketSize *= 2;
        }

        String sql = "INSERT INTO matches (tournament_id, round_number, bracket_index, home_team_id, away_team_id, match_date) VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Menonaktifkan auto-commit untuk transaction management
            conn.setAutoCommit(false);

            // Generate struktur bracket kosong untuk semua round
            for (int i = 1; i <= bracketSize / 2; i *= 2) {
                int roundNumber = i;

                for (int matchIdx = 1; matchIdx <= i; matchIdx++) {
                    stmt.setInt(1, tournamentId);
                    stmt.setInt(2, roundNumber);
                    stmt.setInt(3, matchIdx);
                    
                    stmt.setNull(4, java.sql.Types.INTEGER);
                    stmt.setNull(5, java.sql.Types.INTEGER);
                    
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();

            // Mengisi round pertama dengan tim yang dipilih
            int firstRound = bracketSize / 2;
            
            String updateSql = "UPDATE matches SET home_team_id = ?, away_team_id = ? " +
                               "WHERE tournament_id = ? AND round_number = ? AND bracket_index = ?";
            
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                for (int i = 0; i < bracketSize / 2; i++) {
                    int homeIndex = i * 2;
                    int awayIndex = i * 2 + 1;

                    Integer homeId = (homeIndex < teamCount) ? teamIds.get(homeIndex) : null;
                    Integer awayId = (awayIndex < teamCount) ? teamIds.get(awayIndex) : null;

                    if (homeId != null) updateStmt.setInt(1, homeId); 
                    else updateStmt.setNull(1, java.sql.Types.INTEGER);

                    if (awayId != null) updateStmt.setInt(2, awayId);
                    else updateStmt.setNull(2, java.sql.Types.INTEGER);

                    updateStmt.setInt(3, tournamentId);
                    updateStmt.setInt(4, firstRound); 
                    updateStmt.setInt(5, i + 1);
                    
                    updateStmt.addBatch();
                }
                updateStmt.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Memajukan pemenang match ke round berikutnya secara otomatis setelah match selesai
    public void advanceWinnerToNextRound(Match currentMatch) {
        int winnerTeamId = (currentMatch.getHomeScore() > currentMatch.getAwayScore()) 
                           ? currentMatch.getHomeTeamId() 
                           : currentMatch.getAwayTeamId();

        int currentRound = currentMatch.getRoundNumber();
        int nextRound = currentRound / 2; 

        if (nextRound < 1) {
            return; 
        }

        int nextBracketIndex = (currentMatch.getBracketIndex() + 1) / 2;

        boolean isHomeSlot = (currentMatch.getBracketIndex() % 2 != 0);
        String columnTarget = isHomeSlot ? "home_team_id" : "away_team_id";

        String sql = "UPDATE matches SET " + columnTarget + " = ? " +
                     "WHERE tournament_id = ? AND round_number = ? AND bracket_index = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, winnerTeamId);
            stmt.setInt(2, currentMatch.getTournamentId());
            stmt.setInt(3, nextRound);
            stmt.setInt(4, nextBracketIndex);
            
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                System.err.println("Warning: Failed to advance winner. Check bracket structure.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Mengambil semua match yang sudah selesai menggunakan multiple JOIN untuk ditampilkan di History
    public List<Match> getAllFinishedMatches() {
        List<Match> list = new ArrayList<>();
        
        String sql = "SELECT m.*, t.name as tour_name, th.name as home_name, ta.name as away_name " +
                     "FROM matches m " +
                     "JOIN tournaments t ON m.tournament_id = t.id " +
                     "LEFT JOIN teams th ON m.home_team_id = th.id " +
                     "LEFT JOIN teams ta ON m.away_team_id = ta.id " +
                     "WHERE m.is_finished = 1 " +
                     "ORDER BY m.id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Match m = new Match(
                    rs.getInt("id"),
                    rs.getInt("tournament_id"),
                    rs.getString("tour_name"),
                    rs.getInt("home_team_id"),
                    rs.getString("home_name"),
                    rs.getInt("away_team_id"),
                    rs.getString("away_name"),
                    rs.getInt("round_number"),
                    rs.getInt("bracket_index"),
                    rs.getBoolean("is_finished"),
                    rs.getInt("home_score"),
                    rs.getInt("away_score"),
                    rs.getString("match_date")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Mengambil statistik pemain (total poin dan foul) menggunakan SUM dan COUNT dengan CASE expression
    public List<Player> getPlayerStatsByMatch(int matchId, int teamId) {
        List<Player> list = new ArrayList<>();
        
        String sql = "SELECT p.id, p.name, p.jersey_number, p.position, " +
                     "COALESCE(SUM(CASE WHEN me.event_type = 'SCORE' THEN me.event_value ELSE 0 END), 0) as total_points, " +
                     "COUNT(CASE WHEN me.event_type = 'FOUL' THEN 1 END) as total_fouls " +
                     "FROM players p " +
                     "LEFT JOIN match_events me ON p.id = me.player_id AND me.match_id = ? " +
                     "WHERE p.team_id = ? " +
                     "GROUP BY p.id";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, matchId);
            stmt.setInt(2, teamId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Player p = new Player(
                        rs.getInt("id"),
                        teamId,
                        rs.getString("name"),
                        rs.getInt("jersey_number"),
                        rs.getString("position")
                    );
                    p.setMatchPoints(rs.getInt("total_points"));
                    p.setMatchFouls(rs.getInt("total_fouls"));
                    
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Mengupdate state waktu match (quarter dan remaining seconds) untuk fitur Auto-Save
    public void updateMatchState(int matchId, int quarter, int remainingSeconds) {
        String sql = "UPDATE matches SET current_quarter = ?, remaining_seconds = ? WHERE id = ?";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quarter);
            stmt.setInt(2, remainingSeconds);
            stmt.setInt(3, matchId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Mengambil rekap skor per quarter per tim menggunakan GROUP BY untuk breakdown skor di dashboard
    public Map<Integer, Map<Integer, Integer>> getQuarterScores(int matchId) {
        Map<Integer, Map<Integer, Integer>> result = new HashMap<>();
        
        String sql = "SELECT p.team_id, me.quarter, SUM(me.event_value) as total " +
                     "FROM match_events me " +
                     "JOIN players p ON me.player_id = p.id " +
                     "WHERE me.match_id = ? AND me.event_type = 'SCORE' " +
                     "GROUP BY p.team_id, me.quarter " +
                     "ORDER BY me.quarter ASC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, matchId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    int tId = rs.getInt("team_id");
                    int q = rs.getInt("quarter");
                    int val = rs.getInt("total");
                    
                    result.putIfAbsent(tId, new HashMap<>());
                    result.get(tId).put(q, val);
                }
            }
        } catch(SQLException e) { 
            e.printStackTrace(); 
        }
        return result;
    }
}
