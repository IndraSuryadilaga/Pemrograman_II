package controller;

import dao.MatchDao;
import model.Match;
import model.Player;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import helper.AlertHelper;
import helper.PdfCreator;

// Controller mengatur logika tampilan History dan menghubungkan View dengan Model melalui DAO
public class HistoryController {

    // Komponen UI tabel utama yang di-bind dari HistoryView.fxml
    @FXML private TableView<Match> tableHistory;
    
    // Kolom-kolom tabel utama untuk menampilkan informasi match
    @FXML private TableColumn<Match, String> colTournament;
    @FXML private TableColumn<Match, String> colRound;
    @FXML private TableColumn<Match, String> colHome;
    @FXML private TableColumn<Match, String> colAway;
    @FXML private TableColumn<Match, String> colScore;
    @FXML private TableColumn<Match, String> colTime;

    // Komponen UI untuk tabel detail statistik pemain
    @FXML private Label lblDetailHome, lblDetailAway;
    
    // Tabel statistik tim home
    @FXML private TableView<Player> tableHomeStats;
    @FXML private TableColumn<Player, Integer> colHNum;
    @FXML private TableColumn<Player, String> colHName;
    @FXML private TableColumn<Player, Integer> colHPoint;
    @FXML private TableColumn<Player, Integer> colHFoul;

    // Tabel statistik tim away
    @FXML private TableView<Player> tableAwayStats;
    @FXML private TableColumn<Player, Integer> colANum;
    @FXML private TableColumn<Player, String> colAName;
    @FXML private TableColumn<Player, Integer> colAPoint;
    @FXML private TableColumn<Player, Integer> colAFoul;

    // Controller menggunakan DAO untuk mengakses database
    private MatchDao matchDao;

    // Inisialisasi komponen DAO, setup kolom tabel, load data, dan setup event listener
    @FXML
    public void initialize() {
        matchDao = new MatchDao();
        setupColumns();
        loadHistory();
        
        // Event listener untuk menangani perubahan selection di tabel utama menggunakan lambda
        // Saat user memilih match, otomatis load detail statistik pemain
        tableHistory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadMatchDetails(newVal);
            }
        });
    }

    // Setup cell value factory untuk semua kolom tabel menggunakan PropertyValueFactory dan custom lambda
    private void setupColumns() {
        // Setup kolom tabel utama menggunakan PropertyValueFactory untuk binding langsung ke property Model
        colTournament.setCellValueFactory(new PropertyValueFactory<>("tournamentName"));
        colHome.setCellValueFactory(new PropertyValueFactory<>("homeTeamName"));
        colAway.setCellValueFactory(new PropertyValueFactory<>("awayTeamName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("matchDate"));

        // Custom cell value factory untuk kolom Round: konversi round number ke nama yang user-friendly
        colRound.setCellValueFactory(cellData -> {
            int r = cellData.getValue().getRoundNumber();
            String roundName = (r == 1) ? "FINAL" : (r == 2) ? "Semi Final" : "Round " + r;
            return new SimpleStringProperty(roundName);
        });

        // Custom cell value factory untuk kolom Score: format skor menjadi "homeScore - awayScore"
        colScore.setCellValueFactory(cellData -> {
            Match m = cellData.getValue();
            return new SimpleStringProperty(m.getHomeScore() + " - " + m.getAwayScore());
        });
        
        // Setup kolom tabel detail statistik tim home
        colHNum.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber"));
        colHName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colHPoint.setCellValueFactory(new PropertyValueFactory<>("matchPoints"));
        colHFoul.setCellValueFactory(new PropertyValueFactory<>("matchFouls"));

        // Setup kolom tabel detail statistik tim away
        colANum.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber"));
        colAName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAPoint.setCellValueFactory(new PropertyValueFactory<>("matchPoints"));
        colAFoul.setCellValueFactory(new PropertyValueFactory<>("matchFouls"));
    }

    // Memuat semua match yang sudah selesai dari database dan menampilkannya di tabel utama
    @FXML
    private void loadHistory() {
        // Convert List ke ObservableList untuk auto-update UI saat data berubah
        tableHistory.setItems(FXCollections.observableArrayList(matchDao.getAllFinishedMatches()));
        
        // Reset detail statistik saat reload data utama
        tableHomeStats.getItems().clear();
        tableAwayStats.getItems().clear();
        lblDetailHome.setText("Statistik Home");
        lblDetailAway.setText("Statistik Away");
    }

    // Memuat detail statistik pemain untuk match yang dipilih dari database
    private void loadMatchDetails(Match m) {
        // Update label dengan nama tim yang sesuai
        lblDetailHome.setText("Statistik: " + m.getHomeTeamName());
        lblDetailAway.setText("Statistik: " + m.getAwayTeamName());

        // Load statistik pemain tim home menggunakan DAO dengan aggregation query
        tableHomeStats.setItems(FXCollections.observableArrayList(
            matchDao.getPlayerStatsByMatch(m.getId(), m.getHomeTeamId())
        ));

        // Load statistik pemain tim away menggunakan DAO dengan aggregation query
        tableAwayStats.setItems(FXCollections.observableArrayList(
            matchDao.getPlayerStatsByMatch(m.getId(), m.getAwayTeamId())
        ));
    }

    // Menangani export laporan match ke PDF menggunakan FileChooser dan PdfCreator
    @FXML
    private void handleExportPDF() {
        Match selectedMatch = tableHistory.getSelectionModel().getSelectedItem();
        
        // Validasi: pastikan user sudah memilih match
        if (selectedMatch == null) {
        	AlertHelper.showWarning("Pilih Pertandingan", "Silakan klik salah satu pertandingan di tabel riwayat terlebih dahulu.");
            return;
        }

        // Ambil data statistik dari tabel yang sudah di-load
        List<Player> homeStats = tableHomeStats.getItems();
        List<Player> awayStats = tableAwayStats.getItems();

        // Validasi: pastikan detail statistik sudah di-load
        if (homeStats.isEmpty() && awayStats.isEmpty()) {
        	AlertHelper.showWarning("Data Kosong", "Belum ada detail statistik yang dimuat. Klik baris pertandingan dulu.");
            return;
        }

        // Setup FileChooser untuk memilih lokasi penyimpanan PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        // Set nama file default: Match_ID_Team_vs_Team.pdf
        fileChooser.setInitialFileName("Match_" + selectedMatch.getId() + "_" + selectedMatch.getHomeTeamName() + "_vs_" + selectedMatch.getAwayTeamName() + ".pdf");
        
        // Tampilkan dialog save dan ambil file yang dipilih user
        File file = fileChooser.showSaveDialog(tableHistory.getScene().getWindow());

        // Jika user memilih file (tidak cancel), generate PDF
        if (file != null) {
            try {
                // Generate PDF menggunakan PdfCreator utility class
                PdfCreator.generateMatchReport(selectedMatch, homeStats, awayStats, file.getAbsolutePath());
                AlertHelper.showWarning("Sukses", "Laporan berhasil disimpan di:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                AlertHelper.showWarning("Error", "Gagal membuat PDF: " + e.getMessage());
            }
        }
    }
}