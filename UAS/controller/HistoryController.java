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

import util.PdfCreator;

public class HistoryController {

    // --- TABEL UTAMA ---
    @FXML private TableView<Match> tableHistory;
    @FXML private TableColumn<Match, Integer> colId;
    @FXML private TableColumn<Match, String> colTournament;
    @FXML private TableColumn<Match, String> colRound;
    @FXML private TableColumn<Match, String> colHome;
    @FXML private TableColumn<Match, String> colAway;
    @FXML private TableColumn<Match, String> colScore;
    @FXML private TableColumn<Match, String> colTime;

    // --- TABEL DETAIL ---
    @FXML private Label lblDetailHome, lblDetailAway;
    @FXML private TableView<Player> tableHomeStats;
    @FXML private TableColumn<Player, String> colHName;
    @FXML private TableColumn<Player, Integer> colHPoint;
    @FXML private TableColumn<Player, Integer> colHFoul;

    @FXML private TableView<Player> tableAwayStats;
    @FXML private TableColumn<Player, String> colAName;
    @FXML private TableColumn<Player, Integer> colAPoint;
    @FXML private TableColumn<Player, Integer> colAFoul;
    @FXML private TableColumn<Player, Integer> colHNum;
    @FXML private TableColumn<Player, Integer> colANum;

    private MatchDao matchDao;

    @FXML
    public void initialize() {
        matchDao = new MatchDao();
        setupColumns();
        loadHistory();
        
        // Listener Seleksi Tabel
        tableHistory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadMatchDetails(newVal);
            }
        });
    }

    private void setupColumns() {
        // --- Tabel Utama ---
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTournament.setCellValueFactory(new PropertyValueFactory<>("tournamentName")); // Sesuai field baru
        colHome.setCellValueFactory(new PropertyValueFactory<>("homeTeamName"));
        colAway.setCellValueFactory(new PropertyValueFactory<>("awayTeamName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("matchDate")); // Sesuai field baru

        colRound.setCellValueFactory(cellData -> {
            int r = cellData.getValue().getRoundNumber();
            String roundName = (r == 1) ? "FINAL" : (r == 2) ? "Semi Final" : "Round " + r;
            return new SimpleStringProperty(roundName);
        });

        colScore.setCellValueFactory(cellData -> {
            Match m = cellData.getValue();
            return new SimpleStringProperty(m.getHomeScore() + " - " + m.getAwayScore());
        });
        
        // --- Tabel Detail Home ---
        colHName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colHNum.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber"));
        colHPoint.setCellValueFactory(new PropertyValueFactory<>("matchPoints"));
        colHFoul.setCellValueFactory(new PropertyValueFactory<>("matchFouls"));

        // --- Tabel Detail Away ---
        colAName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colANum.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber"));
        colAPoint.setCellValueFactory(new PropertyValueFactory<>("matchPoints"));
        colAFoul.setCellValueFactory(new PropertyValueFactory<>("matchFouls"));
    }

    @FXML
    private void loadHistory() {
        tableHistory.setItems(FXCollections.observableArrayList(matchDao.getAllFinishedMatches()));
        
        // Bersihkan detail saat reload
        tableHomeStats.getItems().clear();
        tableAwayStats.getItems().clear();
        lblDetailHome.setText("Statistik Home");
        lblDetailAway.setText("Statistik Away");
    }

    private void loadMatchDetails(Match m) {
        // Update Label
        lblDetailHome.setText("Statistik: " + m.getHomeTeamName());
        lblDetailAway.setText("Statistik: " + m.getAwayTeamName());

        // Load Data Statistik dari DAO
        // Ingat method baru: getPlayerStatsByMatch(matchId, teamId)
        tableHomeStats.setItems(FXCollections.observableArrayList(
            matchDao.getPlayerStatsByMatch(m.getId(), m.getHomeTeamId())
        ));

        tableAwayStats.setItems(FXCollections.observableArrayList(
            matchDao.getPlayerStatsByMatch(m.getId(), m.getAwayTeamId())
        ));
    }
    
    @FXML
    private void handleExportPDF() {
        // 1. Ambil Match yang sedang dipilih di tabel
        Match selectedMatch = tableHistory.getSelectionModel().getSelectedItem();
        
        if (selectedMatch == null) {
            showAlert("Pilih Pertandingan", "Silakan klik salah satu pertandingan di tabel riwayat terlebih dahulu.");
            return;
        }

        // 2. Ambil Data Detail (Pemain Home & Away)
        // Kita bisa ambil dari TableView statistik yang sudah ada datanya
        List<Player> homeStats = tableHomeStats.getItems();
        List<Player> awayStats = tableAwayStats.getItems();

        if (homeStats.isEmpty() && awayStats.isEmpty()) {
            showAlert("Data Kosong", "Belum ada detail statistik yang dimuat. Klik baris pertandingan dulu.");
            return;
        }

        // 3. Buka File Chooser (Mau simpan di mana?)
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        // Nama file default: Match_ID.pdf
        fileChooser.setInitialFileName("Match_" + selectedMatch.getId() + "_" + selectedMatch.getHomeTeamName() + "_vs_" + selectedMatch.getAwayTeamName() + ".pdf");
        
        File file = fileChooser.showSaveDialog(tableHistory.getScene().getWindow());

        if (file != null) {
            try {
                // 4. GENERATE PDF!
                PdfCreator.generateMatchReport(selectedMatch, homeStats, awayStats, file.getAbsolutePath());
                
                showAlert("Sukses", "Laporan berhasil disimpan di:\n" + file.getAbsolutePath());
                
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Gagal membuat PDF: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}