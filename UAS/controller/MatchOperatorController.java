package controller;

import dao.MatchDao;
import dao.PlayerDao;
import model.Match;
import model.Player;
import model.rules.SportStrategy;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.List;

public class MatchOperatorController {

    // --- UI COMPONENTS ---
    @FXML private Label lblHomeName, lblAwayName, lblHomeScore, lblAwayScore;
    @FXML private Label lblQuarter, lblTimer;
    @FXML private Button btnTimerControl, btnNextQuarter, btnFinish;
    @FXML private FlowPane fpHomePlayers, fpAwayPlayers;
    
    // UI Overlay (Popup)
    @FXML private VBox actionOverlay;
    @FXML private Label lblSelectedPlayer;
    @FXML private Button btnScore1, btnScore2, btnScore3;

    // --- LOGIC VARIABLES ---
    private Match currentMatch;
    private PlayerDao playerDao;
    private MatchDao matchDao;
    private SportStrategy gameRules;
    private Player selectedPlayer;
    private boolean isHomeTeamAction;

    // --- VARIABEL WAKTU ---
    private Timeline timeline;
    private static final int QUARTER_DURATION = 10 * 60; // 10 Menit (Standard FIBA)
    private static final int OVERTIME_DURATION = 5 * 60; // 5 Menit (OT)
    
    // State untuk Break Time (Istirahat)
    private boolean isBreakTime = false;
    private int breakSecondsCounter = 0;

    @FXML
    public void initialize() {
        playerDao = new PlayerDao();
        matchDao = new MatchDao();
        
        // Setup Timeline (Loop setiap 1 detik)
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    // === 1. INITIALIZATION & LOAD DATA ===
    
    public void setMatchData(Match match, String sportName) {
        this.currentMatch = match;
        this.gameRules = model.rules.RuleFactory.getStrategy(sportName);

        lblHomeName.setText(match.getHomeTeamName());
        lblAwayName.setText(match.getAwayTeamName());
        
        // Cek apakah data Quarter/Waktu di DB masih kosong/default (misal 0)
        // Jika ya, inisialisasi ke Q1 menit ke-10.
        if (currentMatch.getCurrentQuarter() < 1) {
            currentMatch.setCurrentQuarter(1);
            currentMatch.setRemainingSeconds(QUARTER_DURATION);
        }
        
        // Reset state break time setiap kali masuk halaman
        isBreakTime = false;
        
        // Update UI sesuai data terakhir dari DB
        updateQuarterLabel();
        updateClockDisplay();
        updateScoreBoard();
        
        // Load Pemain
        loadPlayersToGrid(match.getHomeTeamId(), fpHomePlayers, true);
        loadPlayersToGrid(match.getAwayTeamId(), fpAwayPlayers, false);
        
        // Atur tombol (+1, +2, +3) sesuai aturan olahraga
        configureButtonsByRules();

        // Default Pause saat baru masuk (User harus klik Start)
        setGameActive(false); 
        
        // Cek jika match sudah selesai sebelumnya
        if (currentMatch.isFinished()) {
            lblQuarter.setText("FINAL");
            lblTimer.setText("00:00");
            btnTimerControl.setDisable(true);
            btnNextQuarter.setDisable(true);
            btnFinish.setDisable(true); // Sudah finish
        }
    }

    // === 2. TIMER LOGIC ===

    @FXML
    private void handleTimerToggle() {
        if (isBreakTime) return; // Tombol Start/Pause tidak fungsi saat istirahat

        if (currentMatch.isTimerRunning()) {
            // -- PAUSE --
            timeline.stop();
            currentMatch.setTimerRunning(false);
            
            btnTimerControl.setText("▶ RESUME");
            btnTimerControl.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
            
            setGameActive(false); // Matikan input skor saat pause
            
            // [IMPORTANT] SIMPAN STATE KE DB SAAT PAUSE
            saveMatchState();
            
        } else {
            // -- START --
            timeline.play();
            currentMatch.setTimerRunning(true);
            
            btnTimerControl.setText("⏸ PAUSE");
            btnTimerControl.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
            
            setGameActive(true); // Hidupkan input skor
        }
    }
    
    // Dipanggil setiap detik oleh Timeline
    private void updateTimer() {
        if (isBreakTime) {
            // Logic Waktu Istirahat (Hitung Maju)
            breakSecondsCounter++;
            int m = breakSecondsCounter / 60;
            int s = breakSecondsCounter % 60;
            
            lblTimer.setText(String.format("BREAK\n%02d:%02d", m, s));
            lblTimer.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #95a5a6;"); 
            
        } else {
            // Logic Waktu Game (Hitung Mundur)
            int timeLeft = currentMatch.getRemainingSeconds();
            
            if (timeLeft > 0) {
                currentMatch.setRemainingSeconds(timeLeft - 1);
                updateClockDisplay();
            } else {
                // Waktu Habis (00:00) -> Masuk Mode Istirahat
                enterBreakMode();
            }
        }
    }

    private void enterBreakMode() {
        isBreakTime = true;
        breakSecondsCounter = 0;
        
        currentMatch.setTimerRunning(false); 
        setGameActive(false); 
        
        java.awt.Toolkit.getDefaultToolkit().beep(); // Bunyi Bel
        
        btnTimerControl.setDisable(true);
        btnTimerControl.setText("ISTIRAHAT");
        
        // Cek status quarter untuk menentukan tombol selanjutnya
        checkQuarterStatus();
    }

    private void checkQuarterStatus() {
        int q = currentMatch.getCurrentQuarter();

        if (q < 4) {
            // Quarter 1, 2, 3 Selesai -> Lanjut Next Q
            btnNextQuarter.setDisable(false);
            lblQuarter.setText("END Q" + q);
        } else {
            // Quarter 4 (atau OT) Selesai
            if (currentMatch.getHomeScore() == currentMatch.getAwayScore()) {
                // SERI -> OVERTIME
                lblQuarter.setText("TIED");
                btnNextQuarter.setText("START OT");
                btnNextQuarter.setDisable(false);
            } else {
                // ADA PEMENANG -> GAME OVER
                lblQuarter.setText("FINAL");
                btnNextQuarter.setDisable(true);
                btnFinish.setDisable(false);
                
                timeline.stop(); // Stop timeline total
                saveMatchState(); // Simpan state akhir
            }
        }
    }

    @FXML
    private void handleNextQuarter() {
        // Keluar dari Break Mode
        isBreakTime = false;
        timeline.stop(); 
        
        // Naikkan Quarter
        int nextQ = currentMatch.getCurrentQuarter() + 1;
        currentMatch.setCurrentQuarter(nextQ);
        
        // Set Waktu Baru (OT atau Normal)
        if (nextQ > 4) {
            currentMatch.setRemainingSeconds(OVERTIME_DURATION);
        } else {
            currentMatch.setRemainingSeconds(QUARTER_DURATION);
        }
        
        // Update UI
        updateQuarterLabel();
        updateClockDisplay();
        lblTimer.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");
        
        // Reset Tombol
        btnNextQuarter.setDisable(true);
        btnTimerControl.setDisable(false);
        btnTimerControl.setText("▶ START");
        btnTimerControl.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        
        // [IMPORTANT] SIMPAN KE DB SETELAH RESET QUARTER
        saveMatchState();
    }

    // === 3. SCORING & ACTIONS ===

    @FXML private void handleActionScore1() { processScore(1); }
    @FXML private void handleActionScore2() { processScore(2); }
    @FXML private void handleActionScore3() { processScore(3); }

    private void processScore(int pointsInput) {
        if (selectedPlayer == null) return;
        java.awt.Toolkit.getDefaultToolkit().beep(); 

        int currentScore = isHomeTeamAction ? currentMatch.getHomeScore() : currentMatch.getAwayScore();
        int newScore = gameRules.calculateNewScore(currentScore, pointsInput);
        int actualPointsAdded = newScore - currentScore;
        
        // Update Skor di Object Match & DB Matches
        if (isHomeTeamAction) {
            currentMatch.setHomeScore(newScore);
            matchDao.updateScore(currentMatch.getId(), newScore, currentMatch.getAwayScore()); 
        } else {
            currentMatch.setAwayScore(newScore);
            matchDao.updateScore(currentMatch.getId(), currentMatch.getHomeScore(), newScore);
        }

        // [IMPORTANT] CATAT EVENT DENGAN INFO QUARTER
        if (actualPointsAdded != 0) {
            matchDao.addMatchEvent(
                currentMatch.getId(), 
                selectedPlayer.getId(), 
                "SCORE", 
                actualPointsAdded, 
                currentMatch.getCurrentQuarter() // <-- Parameter Baru
            );
        }
        
        updateScoreBoard();
        closeOverlay();
    }
    
    @FXML
    private void handleActionFoul() {
        if (selectedPlayer != null) {
            // Catat Foul dengan Quarter
            matchDao.addMatchEvent(
                currentMatch.getId(), 
                selectedPlayer.getId(), 
                "FOUL", 
                1, 
                currentMatch.getCurrentQuarter()
            );
            
            // Cek Foul Out
            int totalFoul = matchDao.getPlayerFoulCount(currentMatch.getId(), selectedPlayer.getId());
            if (gameRules.isFoulOut(totalFoul)) {
                showAlert("PERINGATAN", selectedPlayer.getName() + " terkena Foul Out (" + totalFoul + ")!");
            }
        }
        closeOverlay();
    }

    // === 4. DEBUG / SKIP TIME ===
    
    @FXML
    private void handleDebugSkip() {
        if (!isBreakTime && !currentMatch.isFinished()) {
            // Lompat ke 5 detik terakhir
            currentMatch.setRemainingSeconds(5);
            updateClockDisplay();
            
            // Auto Start Timer
            if (timeline.getStatus() != Timeline.Status.RUNNING) {
                timeline.play();
            }
            currentMatch.setTimerRunning(true);
            
            btnTimerControl.setText("⏸ PAUSE");
            btnTimerControl.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
            setGameActive(true);
        }
    }

    // === 5. NAVIGATION & FINISH ===

    @FXML
    private void handleBack() {
        try {
            // Stop Timeline
            if (timeline != null) timeline.stop();
            
            // [IMPORTANT] SIMPAN STATE TERAKHIR SEBELUM KELUAR
            saveMatchState();

            // Kembali ke Dashboard
            controller.MainController.getInstance().showDashboard(); 
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    @FXML
    private void handleFinishMatch() {
        if (currentMatch.getHomeScore() == currentMatch.getAwayScore()) {
            showAlert("Error", "Skor masih Seri! Harus ada pemenang.");
            return;
        }

        // Finalisasi Match
        matchDao.updateScore(currentMatch.getId(), currentMatch.getHomeScore(), currentMatch.getAwayScore());
        matchDao.advanceWinnerToNextRound(currentMatch); // Promosi Pemenang
        
        // Simpan state akhir (misal finished flag di DB dihandle di method updateScore/finishMatch DAO)
        // Jika DAO belum handle setFinished, bisa panggil method khusus
        // matchDao.setMatchFinished(currentMatch.getId()); 

        showAlert("Selesai", "Pertandingan selesai! Pemenang melaju ke babak berikutnya.");
        handleBack();
    }

    // === HELPER METHODS ===

    private void saveMatchState() {
        // Simpan Quarter & Waktu saat ini ke DB
        matchDao.updateMatchState(
            currentMatch.getId(), 
            currentMatch.getCurrentQuarter(), 
            currentMatch.getRemainingSeconds()
        );
    }

    private void updateClockDisplay() {
        int totalSeconds = currentMatch.getRemainingSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        lblTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }
    
    private void updateQuarterLabel() {
        int q = currentMatch.getCurrentQuarter();
        lblQuarter.setText(q > 4 ? "OT " + (q-4) : "Q" + q);
    }

    private void updateScoreBoard() {
        lblHomeScore.setText(String.valueOf(currentMatch.getHomeScore()));
        lblAwayScore.setText(String.valueOf(currentMatch.getAwayScore()));
    }

    private void setGameActive(boolean isActive) {
        fpHomePlayers.setDisable(!isActive);
        fpAwayPlayers.setDisable(!isActive);
        double opacity = isActive ? 1.0 : 0.5;
        fpHomePlayers.setOpacity(opacity);
        fpAwayPlayers.setOpacity(opacity);
    }

    private void loadPlayersToGrid(int teamId, FlowPane grid, boolean isHome) {
        grid.getChildren().clear();
        List<Player> players = playerDao.getPlayersByTeam(teamId);
        
        for (Player p : players) {
            Button btn = new Button(p.getJerseyNumber() + "\n" + p.getName());
            btn.setPrefSize(100, 80);
            btn.setWrapText(true);
            
            String color = isHome ? "#3498db" : "#e74c3c";
            btn.setStyle("-fx-base: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
            
            btn.setOnAction(e -> openActionOverlay(p, isHome));
            grid.getChildren().add(btn);
        }
    }

    private void openActionOverlay(Player p, boolean isHome) {
        this.selectedPlayer = p;
        this.isHomeTeamAction = isHome;
        lblSelectedPlayer.setText(p.getName() + " (#" + p.getJerseyNumber() + ")");
        actionOverlay.setVisible(true);
    }
    
    @FXML private void closeOverlay() { 
        actionOverlay.setVisible(false); 
        selectedPlayer = null;
    }
    
    private void configureButtonsByRules() {
        List<Integer> validPoints = gameRules.getValidPointOptions();
        updateButtonVisibility(btnScore1, validPoints.contains(1));
        updateButtonVisibility(btnScore2, validPoints.contains(2));
        updateButtonVisibility(btnScore3, validPoints.contains(3));
    }

    private void updateButtonVisibility(Button btn, boolean isVisible) {
        btn.setVisible(isVisible);
        btn.setManaged(isVisible);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}