package dao;

import model.Match;
import util.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchDao {
    
    // Mengambil Data Match beserta Nama Timnya
    // Mengambil Data Match beserta Nama Timnya (DAN NAMA TURNAMEN)
    public List<Match> getMatchesByTournament(int tournamentId) {
        List<Match> list = new ArrayList<>();
        
        // REVISI SQL: Tambahkan JOIN ke tournaments untuk mengambil tour_name
        String sql = "SELECT m.*, t.name as tour_name, th.name as home_name, ta.name as away_name " +
                     "FROM matches m " +
                     "JOIN tournaments t ON m.tournament_id = t.id " + // <-- JOIN BARU
                     "LEFT JOIN teams th ON m.home_team_id = th.id " +
                     "LEFT JOIN teams ta ON m.away_team_id = ta.id " +
                     "WHERE m.tournament_id = ? " +
                     "ORDER BY m.round_number DESC, m.bracket_index ASC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tournamentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                // Gunakan Constructor Match TERBARU (yang ada tournamentName & matchDate)
                Match m = new Match(
                    rs.getInt("id"),
                    rs.getInt("tournament_id"),
                    rs.getString("tour_name"), // <-- Kolom ini sekarang ada
                    rs.getInt("home_team_id"),
                    rs.getString("home_name"),
                    rs.getInt("away_team_id"),
                    rs.getString("away_name"),
                    rs.getInt("round_number"),
                    rs.getInt("bracket_index"),
                    rs.getBoolean("is_finished"),
                    rs.getInt("home_score"),
                    rs.getInt("away_score"),
                    rs.getString("match_date") // <-- Kolom waktu
                );
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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
    
    // === FITUR TRANSACTION LOG (MATCH EVENTS) ===

    // 1. Mencatat Event (Foul, Score, dll)
    public boolean addMatchEvent(int matchId, int playerId, String eventType, int value) {
        String sql = "INSERT INTO match_events (match_id, player_id, event_type, event_value, game_time) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, matchId);
            stmt.setInt(2, playerId);
            stmt.setString(3, eventType); // 'FOUL' atau 'SCORE'
            stmt.setInt(4, value);        // 1 jika Foul, atau poin jika Score
            
            // Simulasi Game Time (Ambil jam sistem saat ini)
            String timeNow = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            stmt.setString(5, timeNow);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Menghitung Jumlah Foul Pemain Tertentu (Aggregation Query)
    public int getPlayerFoulCount(int matchId, int playerId) {
        String sql = "SELECT COUNT(*) FROM match_events WHERE match_id = ? AND player_id = ? AND event_type = 'FOUL'";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, matchId);
            stmt.setInt(2, playerId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // === ALGORITMA GENERATE BRACKET (FIXED LOGIC) ===
    public boolean generateBracket(int tournamentId, List<Integer> teamIds) {
        int teamCount = teamIds.size();
        if (teamCount < 2) return false;

        // Hitung Ukuran Bracket (4, 8, 16...)
        int bracketSize = 1;
        while (bracketSize < teamCount) {
            bracketSize *= 2;
        }

        String sql = "INSERT INTO matches (tournament_id, round_number, bracket_index, home_team_id, away_team_id, match_date) VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);

            // 3. GENERATE STRUKTUR BRACKET KOSONG
            // i = jumlah match di ronde tersebut.
            // i=1 (Final), i=2 (Semi), i=4 (Quarter)
            for (int i = 1; i <= bracketSize / 2; i *= 2) {
                
                // --- PERBAIKAN DISINI ---
                // Logika lama: int roundNumber = bracketSize / i / 2; (TERBALIK)
                // Logika baru: Round 1=Final(1 match), Round 2=Semi(2 match). 
                // Jadi roundNumber SAMA DENGAN i.
                int roundNumber = i; 
                // ------------------------

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

            // 4. ISI BABAK PERTAMA DENGAN TIM
            // Round pertama adalah angka terbesar (misal 2 untuk Semi, 4 untuk Quarter)
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
    
    // === ALGORITMA BRACKET ADVANCEMENT (LOGIKA PROMOSI PEMENANG) ===
    public void advanceWinnerToNextRound(Match currentMatch) {
        // 1. Tentukan Siapa Pemenangnya
        int winnerTeamId = (currentMatch.getHomeScore() > currentMatch.getAwayScore()) 
                           ? currentMatch.getHomeTeamId() 
                           : currentMatch.getAwayTeamId();

        System.out.println("--- DEBUG ADVANCE WINNER ---");
        System.out.println("Match Selesai: Round " + currentMatch.getRoundNumber() + ", Index " + currentMatch.getBracketIndex());
        System.out.println("Pemenang ID: " + winnerTeamId);

        // 2. Tentukan Tujuan (Next Match)
        // Logika DB: Round 4 -> 2 -> 1
        int currentRound = currentMatch.getRoundNumber();
        int nextRound = currentRound / 2; 

        if (nextRound < 1) {
            System.out.println("⏹️ Ini adalah Final. Tidak ada next round.");
            return; 
        }

        // Rumus Index: (1,2 -> 1), (3,4 -> 2)
        int nextBracketIndex = (currentMatch.getBracketIndex() + 1) / 2;

        // 3. Tentukan Slot (Home/Away)
        // Ganjil (1, 3) -> Home, Genap (2, 4) -> Away
        boolean isHomeSlot = (currentMatch.getBracketIndex() % 2 != 0);
        String columnTarget = isHomeSlot ? "home_team_id" : "away_team_id";

        System.out.println("Target Update: Round " + nextRound + ", Index " + nextBracketIndex + ", Slot: " + columnTarget);

        // 4. Update Database
        String sql = "UPDATE matches SET " + columnTarget + " = ? " +
                     "WHERE tournament_id = ? AND round_number = ? AND bracket_index = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, winnerTeamId);
            stmt.setInt(2, currentMatch.getTournamentId());
            stmt.setInt(3, nextRound);
            stmt.setInt(4, nextBracketIndex);
            
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                System.out.println("✅ SUKSES! Database berhasil diupdate.");
            } else {
                System.out.println("❌ GAGAL UPDATE! Baris target tidak ditemukan di Database.");
                System.out.println("SARAN: Buat Turnamen BARU agar struktur bracket digenerate ulang dengan benar.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("----------------------------");
    }
    
    // === FITUR HISTORY ===
    public List<Match> getAllFinishedMatches() {
        List<Match> list = new ArrayList<>();
        // Join ke Tournaments untuk dapat nama, dan ambil match_date
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

    // 2. BARU: Ambil Statistik Spesifik Pemain di Match tertentu
    // Mengembalikan object Player yang sudah diisi poin & foul match tersebut
    public List<model.Player> getPlayerStatsByMatch(int matchId, int teamId) {
        List<model.Player> list = new ArrayList<>();
        
        // Query agak kompleks: Kita ambil pemain, lalu hitung sum event SCORE dan count event FOUL
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
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.Player p = new model.Player(
                    rs.getInt("id"),
                    teamId,
                    rs.getString("name"),
                    rs.getInt("jersey_number"),
                    rs.getString("position")
                );
                // Isi statistik transien
                p.setMatchPoints(rs.getInt("total_points"));
                p.setMatchFouls(rs.getInt("total_fouls"));
                
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
}
