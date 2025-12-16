package controller;

import dao.MatchDao;
import dao.TeamDao;
import model.Match;
import model.Team;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class TournamentController {

    // UI Components
    @FXML private ListView<Match> listMatches;
    @FXML private Label lblHomeName, lblAwayName;
    @FXML private Label lblHomeScore, lblAwayScore;
    @FXML private Label lblStatus;
    @FXML private Button btnFinish;

    // Data Helpers
    private MatchDao matchDao;
    private TeamDao teamDao;
    private Match currentMatch;
    
    // Live Score Variables
    private int currentHomeScore = 0;
    private int currentAwayScore = 0;

    @FXML
    public void initialize() {
        matchDao = new MatchDao();
        teamDao = new TeamDao();
        
        loadMatches();

        // Listener: Jika user klik salah satu match di list kiri
        listMatches.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectMatch(newVal);
            }
        });
    }

    private void loadMatches() {
        // Hardcode Tournament ID 1 (Sesuai database kita tadi)
        List<Match> matches = matchDao.getMatchesByTournament(1);
        ObservableList<Match> items = FXCollections.observableArrayList(matches);
        listMatches.setItems(items);
    }

    private void selectMatch(Match m) {
        this.currentMatch = m;
        
        // Tampilkan nama tim di papan skor tengah
        lblHomeName.setText(m.getHomeTeamName());
        lblAwayName.setText(m.getAwayTeamName());
        
        // Reset skor sementara
        currentHomeScore = m.getHomeScore();
        currentAwayScore = m.getAwayScore();
        updateScoreDisplay();
        
        lblStatus.setText("Pertandingan dipilih: " + m.toString());
        btnFinish.setDisable(m.isFinished()); // Disable tombol jika match sudah selesai
    }

    // === LOGIKA GENERATE BRACKET (TOMBOL KIRI ATAS) ===
    @FXML
    private void handleGenerate() {
        List<Team> allTeams = teamDao.getAll();
        if (allTeams.size() < 4) {
            lblStatus.setText("Error: Butuh minimal 4 tim untuk generate!");
            return;
        }

        List<Integer> ids = new ArrayList<>();
        for (Team t : allTeams) ids.add(t.getId());

        // Panggil Logic Bracket dari DAO
        if (matchDao.generateBracket(1, ids)) {
            lblStatus.setText("Sukses Generate Bracket Baru!");
            loadMatches(); // Refresh list
        } else {
            lblStatus.setText("Gagal generate (Mungkin sudah ada jadwal?)");
        }
    }

    // === LOGIKA LIVE SCORING (OPERATOR) ===
    
    @FXML private void addHome1() { updateHome(1); }
    @FXML private void addHome2() { updateHome(2); }
    @FXML private void addHome3() { updateHome(3); }
    
    @FXML private void addAway1() { updateAway(1); }
    @FXML private void addAway2() { updateAway(2); }
    @FXML private void addAway3() { updateAway(3); }

    private void updateHome(int points) {
        if (currentMatch == null || currentMatch.isFinished()) return;
        currentHomeScore += points;
        playAudioEffect(); // Efek Suara
        updateScoreDisplay();
    }

    private void updateAway(int points) {
        if (currentMatch == null || currentMatch.isFinished()) return;
        currentAwayScore += points;
        playAudioEffect(); // Efek Suara
        updateScoreDisplay();
    }

    private void updateScoreDisplay() {
        lblHomeScore.setText(String.valueOf(currentHomeScore));
        lblAwayScore.setText(String.valueOf(currentAwayScore));
    }

    // Fitur Suara Sederhana (Bisa diganti MP3 player nanti)
    private void playAudioEffect() {
        // System Beep (Tiiiit!)
        // java.awt.Toolkit.getDefaultToolkit().beep(); 
    	System.out.println("Beeep");
    }

    // === FINISH MATCH ===
    @FXML
    private void handleFinishMatch() {
        if (currentMatch == null) return;

        boolean success = matchDao.updateScore(currentMatch.getId(), currentHomeScore, currentAwayScore);
        
        if (success) {
            lblStatus.setText("Pertandingan Selesai & Disimpan!");
            loadMatches(); // Refresh list agar status terupdate
            btnFinish.setDisable(true);
        } else {
            lblStatus.setText("Gagal menyimpan ke database.");
        }
    }
    
    private String formatQuarterDetails(java.util.Map<Integer, Integer> quarterMap) {
        if (quarterMap == null || quarterMap.isEmpty()) return "";
        
        StringBuilder sb = new StringBuilder("(");
        // Loop quarter 1 sampai 4 (atau lebih jika OT)
        int maxQ = quarterMap.keySet().stream().max(Integer::compare).orElse(4);
        
        for (int i = 1; i <= maxQ; i++) {
            int score = quarterMap.getOrDefault(i, 0);
            sb.append(score);
            if (i < maxQ) sb.append(", ");
        }
        sb.append(")");
        return sb.toString(); // Output contoh: "(10, 15, 12, 20)"
    }
    
    private VBox createMatchNode(Match match) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setMinWidth(160); // Agak dilebarkan biar muat detail skor
        
        // --- LOGIKA BARU: AMBIL DETAIL SKOR PER QUARTER ---
        String homeDetail = "";
        String awayDetail = "";
        
        if (match.getId() != 0) { // Hanya jika match valid
             java.util.Map<Integer, java.util.Map<Integer, Integer>> details = matchDao.getQuarterScores(match.getId());
             
             // Ambil detail per tim
             homeDetail = formatQuarterDetails(details.get(match.getHomeTeamId()));
             awayDetail = formatQuarterDetails(details.get(match.getAwayTeamId()));
        }
        // -------------------------------------------------

        // --- BARIS 1: HOME TEAM ---
        HBox homeBox = new HBox(10);
        homeBox.setAlignment(Pos.CENTER_LEFT);
        Label lblHome = new Label(match.getHomeTeamName().isEmpty() ? "TBD" : match.getHomeTeamName());
        lblHome.setStyle("-fx-font-weight: bold;");
        Label lblHomeScore = new Label(String.valueOf(match.getHomeScore()));
        
        // Label Detail Kecil (Home)
        Label lblHomeQ = new Label(homeDetail);
        lblHomeQ.setStyle("-fx-font-size: 9px; -fx-text-fill: grey;"); // Font kecil
        
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        // Susun: Nama Tim --Spacer-- Skor (Detail)
        VBox homeRight = new VBox(0); // VBox untuk skor & detail
        homeRight.setAlignment(Pos.CENTER_RIGHT);
        homeRight.getChildren().addAll(lblHomeScore, lblHomeQ);
        
        homeBox.getChildren().addAll(lblHome, spacer1, homeRight);

        // --- SEPARATOR ---
        Separator sep = new Separator();

        // --- BARIS 2: AWAY TEAM ---
        HBox awayBox = new HBox(10);
        awayBox.setAlignment(Pos.CENTER_LEFT);
        Label lblAway = new Label(match.getAwayTeamName().isEmpty() ? "TBD" : match.getAwayTeamName());
        lblAway.setStyle("-fx-font-weight: bold;");
        Label lblAwayScore = new Label(String.valueOf(match.getAwayScore()));
        
        // Label Detail Kecil (Away)
        Label lblAwayQ = new Label(awayDetail);
        lblAwayQ.setStyle("-fx-font-size: 9px; -fx-text-fill: grey;"); // Font kecil
        
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // Susun: Nama Tim --Spacer-- Skor (Detail)
        VBox awayRight = new VBox(0);
        awayRight.setAlignment(Pos.CENTER_RIGHT);
        awayRight.getChildren().addAll(lblAwayScore, lblAwayQ);
        
        awayBox.getChildren().addAll(lblAway, spacer2, awayRight);

        // --- INFO TAMBAHAN (WAKTU/STATUS) ---
        Label lblStatus = new Label();
        if (match.isFinished()) {
            lblStatus.setText("FINAL");
            lblStatus.setStyle("-fx-font-size: 10px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else if (match.getHomeTeamName().isEmpty() || match.getAwayTeamName().isEmpty()) {
             lblStatus.setText("Waiting...");
             lblStatus.setStyle("-fx-font-size: 10px; -fx-text-fill: grey;");
        } else {
            // Tampilkan Quarter saat ini jika sedang main
            lblStatus.setText("LIVE - Q" + match.getCurrentQuarter());
            lblStatus.setStyle("-fx-font-size: 10px; -fx-text-fill: #e67e22; -fx-font-weight: bold;");
            
            // Tambah event klik untuk membuka Match Operator
            card.setOnMouseClicked(e -> controller.MainController.getInstance().openMatchOperator(match));
            card.setCursor(javafx.scene.Cursor.HAND);
        }

        card.getChildren().addAll(lblStatus, homeBox, sep, awayBox);
        return card;
    }
}