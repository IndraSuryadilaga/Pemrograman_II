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
    
    // HAPUS colId karena sudah tidak ada di FXML
    // @FXML private TableColumn<Match, Integer> colId; 
    
    @FXML private TableColumn<Match, String> colTournament;
    @FXML private TableColumn<Match, String> colRound;
    @FXML private TableColumn<Match, String> colHome;
    @FXML private TableColumn<Match, String> colAway;
    @FXML private TableColumn<Match, String> colScore;
    @FXML private TableColumn<Match, String> colTime;

    // --- TABEL DETAIL ---
    @FXML private Label lblDetailHome, lblDetailAway;
    
    @FXML private TableView<Player> tableHomeStats;
    @FXML private TableColumn<Player, Integer> colHNum; // Kolom Nomor Punggung
    @FXML private TableColumn<Player, String> colHName;
    @FXML private TableColumn<Player, Integer> colHPoint;
    @FXML private TableColumn<Player, Integer> colHFoul;

    @FXML private TableView<Player> tableAwayStats;
    @FXML private TableColumn<Player, Integer> colANum; // Kolom Nomor Punggung
    @FXML private TableColumn<Player, String> colAName;
    @FXML private TableColumn<Player, Integer> colAPoint;
    @FXML private TableColumn<Player, Integer> colAFoul;

    private MatchDao matchDao;

    @FXML
    public void initialize() {
        matchDao = new MatchDao();
        setupColumns();
        loadHistory();
        
        tableHistory.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadMatchDetails(newVal);
            }
        });
    }

    private void setupColumns() {
        // --- Tabel Utama ---
        // HAPUS SETUP colId
        // colId.setCellValueFactory(new PropertyValueFactory<>("id")); 

        colTournament.setCellValueFactory(new PropertyValueFactory<>("tournamentName"));
        colHome.setCellValueFactory(new PropertyValueFactory<>("homeTeamName"));
        colAway.setCellValueFactory(new PropertyValueFactory<>("awayTeamName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("matchDate"));

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
        colHNum.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber")); // Setup No Punggung
        colHName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colHPoint.setCellValueFactory(new PropertyValueFactory<>("matchPoints"));
        colHFoul.setCellValueFactory(new PropertyValueFactory<>("matchFouls"));

        // --- Tabel Detail Away ---
        colANum.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber")); // Setup No Punggung
        colAName.setCellValueFactory(new PropertyValueFactory<>("name"));
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
        tableHomeStats.setItems(FXCollections.observableArrayList(
            matchDao.getPlayerStatsByMatch(m.getId(), m.getHomeTeamId())
        ));

        tableAwayStats.setItems(FXCollections.observableArrayList(
            matchDao.getPlayerStatsByMatch(m.getId(), m.getAwayTeamId())
        ));
    }

    @FXML
    private void handleExportPDF() {
        Match selectedMatch = tableHistory.getSelectionModel().getSelectedItem();
        
        if (selectedMatch == null) {
            showAlert("Pilih Pertandingan", "Silakan klik salah satu pertandingan di tabel riwayat terlebih dahulu.");
            return;
        }

        List<Player> homeStats = tableHomeStats.getItems();
        List<Player> awayStats = tableAwayStats.getItems();

        if (homeStats.isEmpty() && awayStats.isEmpty()) {
            showAlert("Data Kosong", "Belum ada detail statistik yang dimuat. Klik baris pertandingan dulu.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        // Nama file default: Match_ID_Team_vs_Team.pdf (ID masih bisa diambil dari object Match)
        fileChooser.setInitialFileName("Match_" + selectedMatch.getId() + "_" + selectedMatch.getHomeTeamName() + "_vs_" + selectedMatch.getAwayTeamName() + ".pdf");
        
        File file = fileChooser.showSaveDialog(tableHistory.getScene().getWindow());

        if (file != null) {
            try {
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