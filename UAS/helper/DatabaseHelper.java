package helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    // URL SQLite
    private static final String URL = "jdbc:sqlite:sportaV2.db"; 
    
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Driver SQLite
                Class.forName("org.sqlite.JDBC"); 
                connection = DriverManager.getConnection(URL);
                
                // Create Table Otomatis saat pertama kali jalan
                createTablesIfNotExist(connection); 
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Gagal koneksi database: " + e.getMessage());
        }
        return connection;
    }

    // Method ini memastikan tabel dibuat otomatis tanpa perlu import SQL manual
    private static void createTablesIfNotExist(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // 1. Tabel Tournaments
        // Perhatikan: SQLite pakai 'INTEGER PRIMARY KEY AUTOINCREMENT' (bukan AUTO_INCREMENT)
        String sqlTournament = "CREATE TABLE IF NOT EXISTS tournaments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sport_id INTEGER, " +
                "name TEXT, " +
                "start_date TEXT, " +
                "status TEXT)";
        stmt.execute(sqlTournament);

        // 2. Tabel Teams
        String sqlTeam = "CREATE TABLE IF NOT EXISTS teams (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT," +
                "logo_path TEXT)";
        stmt.execute(sqlTeam);

        // 3. Tabel Players
        String sqlPlayer = "CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "team_id INTEGER, " +
                "name TEXT, " +
                "jersey_number INTEGER, " +
                "position TEXT, " +
                "FOREIGN KEY(team_id) REFERENCES teams(id))";
        stmt.execute(sqlPlayer);

        // 4. Tabel Matches
        String sqlMatch = "CREATE TABLE IF NOT EXISTS matches (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tournament_id INTEGER, " +
                "round_number INTEGER, " +
                "bracket_index INTEGER, " +
                "home_team_id INTEGER, " +
                "away_team_id INTEGER, " +
                "home_score INTEGER DEFAULT 0, " +
                "away_score INTEGER DEFAULT 0, " +
                "is_finished INTEGER DEFAULT 0, " +
                "match_date TEXT, " +
                "FOREIGN KEY(tournament_id) REFERENCES tournaments(id))";
        stmt.execute(sqlMatch);
        
        String sqlDetails = "CREATE TABLE IF NOT EXISTS match_details (" +
                "match_id INTEGER, " +
                "team_id INTEGER, " +
                "quarter INTEGER, " +
                "score INTEGER, " +
                "PRIMARY KEY(match_id, team_id, quarter))";
        stmt.execute(sqlDetails);
        
        java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM teams");
        if (rs.next() && rs.getInt(1) == 0) {
            System.out.println("Database kosong. Menambahkan Dummy Data IBL...");
            
            // --- TEAM 1: Pelita Jaya Bakrie ---
            stmt.execute("INSERT INTO teams (name, logo_path) VALUES ('Pelita Jaya Bakrie', '/images/pelita.png')");
            // ID Team = 1 (karena autoincrement pertama)
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (1, 'Andakara Prastawa', 1, 'PG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (1, 'Muhamad Arighi', 4, 'SG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (1, 'Govinda Julian', 11, 'PF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (1, 'Vincent Kosasih', 15, 'C')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (1, 'Agassi Goantara', 7, 'SF')");

            // --- TEAM 2: Satria Muda Pertamina ---
            stmt.execute("INSERT INTO teams (name, logo_path) VALUES ('Satria Muda Pertamina', '/images/satriamuda.png')");
            // ID Team = 2
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (2, 'Arki Dikania Wisnu', 33, 'SF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (2, 'Juan Laurent', 0, 'PF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (2, 'Antoni Erga', 8, 'PG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (2, 'Sandy Ibrahim', 13, 'SG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (2, 'Ali Bagir', 16, 'C')");

            // --- TEAM 3: Prawira Harum Bandung ---
            stmt.execute("INSERT INTO teams (name, logo_path) VALUES ('Prawira Harum Bandung', '/images/prawira.png')");
            // ID Team = 3
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (3, 'Yudha Saputera', 2, 'PG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (3, 'Hans Abraham', 10, 'SG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (3, 'Indra Muhammad', 24, 'SF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (3, 'Pandu Wiguna', 35, 'PF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (3, 'Firdhan Guntara', 5, 'SF')");

            // --- TEAM 4: Dewa United Banten ---
            stmt.execute("INSERT INTO teams (name, logo_path) VALUES ('Dewa United Banten', '/images/dewa.png')");
            // ID Team = 4
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (4, 'Kaleb Ramot Gemilang', 13, 'SF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (4, 'Hardianus Lakudu', 2, 'PG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (4, 'Ferdian Dwi Purwoko', 22, 'PF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (4, 'Firman Dwi Nugroho', 16, 'C')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (4, 'Rio Disi', 9, 'SG')");

            // --- TEAM 5: RANS Simba Bogor ---
            stmt.execute("INSERT INTO teams (name, logo_path) VALUES ('RANS Simba Bogor', '/images/rans.png')");
            // ID Team = 5
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (5, 'Althof Satrio', 8, 'SG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (5, 'Oki Wira Sanjaya', 5, 'SG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (5, 'Januar Kuntara', 22, 'PG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (5, 'Agus Salim', 14, 'C')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (5, 'Alexander Franklyn', 1, 'SF')");

            // --- TEAM 6: Bima Perkasa Jogja ---
            stmt.execute("INSERT INTO teams (name, logo_path) VALUES ('Bima Perkasa Jogja', '/images/bima.png')");
            // ID Team = 6
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (6, 'Avin Kurniawan', 1, 'SG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (6, 'Restu Dwi Purnomo', 11, 'PF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (6, 'Ikram Fadhil', 23, 'SF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (6, 'Ali Mustofa', 32, 'C')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (6, 'Joseph Desmet', 6, 'PG')");

            // --- TEAM 7: Bali United Basketball ---
            stmt.execute("INSERT INTO teams (name, logo_path) VALUES ('Bali United', '/images/bali.png')");
            // ID Team = 7
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (7, 'Abraham Wenas', 4, 'PG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (7, 'Ponsianus Nyoman', 13, 'C')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (7, 'Surliyadin', 52, 'SF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (7, 'Lutfi Eka Koswara', 1, 'SG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (7, 'Rico Aditya', 21, 'PF')");

            // --- TEAM 8: Tangerang Hawks ---
            stmt.execute("INSERT INTO teams (name, logo_path) VALUES ('Tangerang Hawks', '/images/hawks.png')");
            // ID Team = 8
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (8, 'Danny Ray', 3, 'SG')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (8, 'Rizky Effendi', 10, 'SF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (8, 'Habib Tito Aji', 24, 'PF')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (8, 'Teddy Apriyana', 15, 'C')");
            stmt.execute("INSERT INTO players (team_id, name, jersey_number, position) VALUES (8, 'Andreas Kristian', 0, 'PG')");
            
            System.out.println("Dummy Data Berhasil Ditambahkan!");
        }
    }
}