package controller;

import dao.MatchDao;
import dao.TeamDao;
import dao.TournamentDao;
import model.Team;
import helper.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;

// Controller mengatur logika pembuatan turnamen baru dan generate bracket menggunakan transaction management
public class NewTournamentController {

    // Komponen UI yang dari NewTournamentView.fxml
    @FXML private TextField tfTournamentName;
    @FXML private ComboBox<String> cbSport;
    @FXML private ListView<Team> listTeams;
    @FXML private Label lblSelectionCount;
    @FXML private Button btnGenerate;

    // Controller menggunakan DAO untuk mengakses database
    private TeamDao teamDao;
    private MatchDao matchDao;
    private TournamentDao tournamentDao;

    // Inisialisasi komponen DAO, setup ComboBox sport, load tim ke ListView, dan setup multi-selection dengan listener
    @FXML
    public void initialize() {
        teamDao = new TeamDao();
        matchDao = new MatchDao();
        tournamentDao = new TournamentDao();

        // Setup ComboBox dengan opsi olahraga dan auto-select yang pertama
        cbSport.setItems(FXCollections.observableArrayList("Basketball", "Badminton"));
        cbSport.getSelectionModel().selectFirst();

        // Load semua tim dari database ke ListView
        List<Team> teams = teamDao.getAll();
        listTeams.setItems(FXCollections.observableArrayList(teams));
        
        // Aktifkan mode multi-selection agar bisa memilih banyak tim sekaligus
        listTeams.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Listener untuk menghitung jumlah tim yang dipilih secara real-time menggunakan lambda
        listTeams.getSelectionModel().getSelectedItems().addListener((javafx.collections.ListChangeListener.Change<? extends Team> c) -> {
            int count = listTeams.getSelectionModel().getSelectedItems().size();
            lblSelectionCount.setText(count + " Tim dipilih");
        });
    }

    // Menangani proses generate turnamen baru dan bracket pertandingan dengan validasi dan transaction management
    @FXML
    private void handleGenerate() {
        // Validasi input: nama turnamen dan olahraga harus diisi
        String name = tfTournamentName.getText();
        String sport = cbSport.getValue();
        List<Team> selectedTeams = listTeams.getSelectionModel().getSelectedItems();

        if (name.isEmpty() || sport == null) {
        	AlertHelper.showWarning("Error", "Nama turnamen dan olahraga harus diisi!");
            return;
        }

        // Validasi jumlah tim: minimal 4 tim untuk bracket semifinal
        if (selectedTeams.size() < 4) {
        	AlertHelper.showWarning("Error", "Pilih minimal 4 tim untuk membuat bracket!");
            return;
        }
        
        // Validasi jumlah tim harus genap untuk bracket yang valid
        if (selectedTeams.size() % 2 != 0) {
        	AlertHelper.showWarning("Warning", "Jumlah tim ganjil...");
        	return;
        }

        // Proses database transaction: insert turnamen dan generate bracket
        try {
        	// Mapping nama olahraga ke sportId
            int sportId = sport.equals("Basketball") ? 1 : 2;
            
            // Simpan header turnamen baru dan dapatkan ID yang di-generate
            int newTournamentId = tournamentDao.createTournament(name, sportId);
            
            if (newTournamentId != -1) {
                // Ambil ID dari tim yang dipilih untuk generate bracket
                List<Integer> teamIds = new ArrayList<>();
                for (Team t : selectedTeams) {
                    teamIds.add(t.getId());
                }

                // Generate bracket pertandingan menggunakan algoritma bracket generation
                boolean success = matchDao.generateBracket(newTournamentId, teamIds);
                
                if (success) {
                	AlertHelper.showWarning("Sukses", "Turnamen & Bracket Berhasil Dibuat!");
                } else {
                	AlertHelper.showWarning("Error", "Gagal meng-generate jadwal pertandingan.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showWarning("Error", "Terjadi kesalahan database: " + e.getMessage());
        }
    }
}