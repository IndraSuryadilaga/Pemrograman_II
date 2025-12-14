package controller;

import dao.MatchDao;
import dao.TeamDao;
import model.Team;
import util.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewTournamentController {

    @FXML private TextField tfTournamentName;
    @FXML private ComboBox<String> cbSport;
    @FXML private ListView<Team> listTeams;
    @FXML private Label lblSelectionCount;
    @FXML private Button btnGenerate;

    private TeamDao teamDao;
    private MatchDao matchDao;

    @FXML
    public void initialize() {
        teamDao = new TeamDao();
        matchDao = new MatchDao();

        // 1. Setup ComboBox Sport
        cbSport.setItems(FXCollections.observableArrayList("Basketball", "Badminton"));
        cbSport.getSelectionModel().selectFirst();

        // 2. Load Semua Tim ke ListView
        List<Team> teams = teamDao.getAll();
        listTeams.setItems(FXCollections.observableArrayList(teams));
        
        // 3. Aktifkan Mode Multi-Selection (Agar bisa pilih banyak tim)
        listTeams.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Listener: Hitung berapa tim yang dipilih real-time
        listTeams.getSelectionModel().getSelectedItems().addListener((javafx.collections.ListChangeListener.Change<? extends Team> c) -> {
            int count = listTeams.getSelectionModel().getSelectedItems().size();
            lblSelectionCount.setText(count + " Tim dipilih");
        });
    }

    @FXML
    private void handleGenerate() {
        // A. Validasi Input
        String name = tfTournamentName.getText();
        String sport = cbSport.getValue();
        List<Team> selectedTeams = listTeams.getSelectionModel().getSelectedItems();

        if (name.isEmpty() || sport == null) {
            showAlert("Error", "Nama turnamen dan olahraga harus diisi!");
            return;
        }

        // Syarat Bracket: Minimal 4 tim (Semifinal)
        if (selectedTeams.size() < 4) {
            showAlert("Error", "Pilih minimal 4 tim untuk membuat bracket!");
            return;
        }
        
        // Syarat Bracket Ideal: Jumlah tim sebaiknya genap (4, 8, 16)
        // Untuk UAS, kita beri warning saja atau paksa genap, tapi biarkan jalan dulu.
        if (selectedTeams.size() % 2 != 0) {
            showAlert("Warning", "Jumlah tim ganjil (" + selectedTeams.size() + "). Bracket mungkin tidak sempurna (ada Bye). Disarankan 4 atau 8 tim.");
            // Lanjut saja tidak apa-apa untuk demo
        }

        // B. PROSES DATABASE TRANSAKSI
        try {
            // 1. Simpan Header Turnamen Baru
            int newTournamentId = createTournamentHeader(name, sport);
            
            if (newTournamentId != -1) {
                // 2. Ambil ID dari tim yang dipilih
                List<Integer> teamIds = new ArrayList<>();
                for (Team t : selectedTeams) {
                    teamIds.add(t.getId());
                }

                // 3. Generate Bracket Pertandingan
                boolean success = matchDao.generateBracket(newTournamentId, teamIds);
                
                if (success) {
                    showAlert("Sukses", "Turnamen & Bracket Berhasil Dibuat!");
                    // TODO: Arahkan user kembali ke Dashboard (Nanti diatur di MainController)
                } else {
                    showAlert("Error", "Gagal meng-generate jadwal pertandingan.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan database: " + e.getMessage());
        }
    }

    // Helper: Insert Tournament dan return ID-nya (Auto Increment)
    private int createTournamentHeader(String name, String sportName) {
        // Map String Sport ke ID (Simple logic)
        int sportId = sportName.equals("Basketball") ? 1 : 2; 

        String sql = "INSERT INTO tournaments (sport_id, name, start_date, status) VALUES (?, ?, NOW(), 'ONGOING')";
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // PENTING: RETURN_GENERATED_KEYS
            
            stmt.setInt(1, sportId);
            stmt.setString(2, name);
            
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Mengembalikan ID Turnamen Baru (misal: 2, 3, dst)
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Gagal
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}