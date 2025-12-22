package controller;

import dao.MatchDao;
import dao.PlayerDao;
import model.Match;
import model.Player;
import model.rules.SportStrategy;
import helper.AlertHelper;
import model.rules.RuleFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.List;
import helper.SoundHelper;

// Controller mengatur logika operasi match menggunakan SportStrategy (Polymorphism) dengan fitur Auto-Save, Breakdown Skor, Lock Button, dan Advance Winner
public class MatchOperatorController {

    // Komponen UI yang di-bind dari MatchOperatorView.fxml
    @FXML private Button btnBack;
    @FXML private Label lblHomeName, lblAwayName, lblHomeScore, lblAwayScore;
    @FXML private Label lblQuarter, lblTimer;
    @FXML private Button btnTimerControl, btnNextQuarter, btnFinish;
    @FXML private FlowPane fpHomePlayers, fpAwayPlayers;
    @FXML private VBox actionOverlay;
    @FXML private Label lblSelectedPlayer;
    @FXML private Button btnScore1, btnScore2, btnScore3;

    // Controller menggunakan Model dan DAO untuk mengakses data
    private Match currentMatch;
    private PlayerDao playerDao;
    private MatchDao matchDao;
    
    // Menggunakan interface SportStrategy untuk polymorphism
    private SportStrategy gameRules;
    
    // State untuk operasi match
    private Player selectedPlayer;
    private boolean isHomeTeamAction;
    private Timeline timeline;
    
    // Konstanta durasi quarter dan overtime
    private static final int QUARTER_DURATION = 10 * 60;
    private static final int OVERTIME_DURATION = 5 * 60;
    
    // State untuk waktu istirahat antar quarter
    private boolean isBreakTime = false;
    private int breakSecondsCounter = 0;

    // Inisialisasi DAO dan Timeline untuk timer
    @FXML
    public void initialize() {
        playerDao = new PlayerDao();
        matchDao = new MatchDao();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        
    }

    // Mengatur data match dan menginisialisasi UI dengan menggunakan RuleFactory untuk mendapatkan strategi olahraga
    public void setMatchData(Match match, String sportName) {
        this.currentMatch = match;
        this.gameRules = RuleFactory.getStrategy(sportName);

        lblHomeName.setText(match.getHomeTeamName());
        lblAwayName.setText(match.getAwayTeamName());
        
        // Load data quarter dan waktu dari database untuk resume match (Auto-Save Time)
        if (currentMatch.getCurrentQuarter() < 1) {
            currentMatch.setCurrentQuarter(1);
            currentMatch.setRemainingSeconds(QUARTER_DURATION);
        }
        
        // Update UI dengan data dari Model
        updateQuarterLabel();
        updateClockDisplay();
        updateScoreBoard();
        loadPlayersToGrid(match.getHomeTeamId(), fpHomePlayers, true);
        loadPlayersToGrid(match.getAwayTeamId(), fpAwayPlayers, false);
        configureButtonsByRules();

        // Lock Button: tombol Back aktif jika match selesai, mati jika belum selesai
        if (currentMatch.isFinished()) {
            btnBack.setDisable(false);
            btnTimerControl.setDisable(true);
            btnFinish.setDisable(true);
            lblQuarter.setText("FINAL");
        } else {
            btnBack.setDisable(true); 
            setGameActive(false); 
        }
    }

    // Toggle timer (Start/Pause) dan menyimpan state waktu saat pause untuk Auto-Save
    @FXML
    private void handleTimerToggle() {
        if (isBreakTime) return;

        if (currentMatch.isTimerRunning()) {
            // Pause timer dan simpan state ke database
            timeline.stop();
            currentMatch.setTimerRunning(false);
            btnTimerControl.setText("▶ RESUME");
            setGameActive(false);
            matchDao.updateMatchState(currentMatch.getId(), currentMatch.getCurrentQuarter(), currentMatch.getRemainingSeconds());
        } else {
            // Start timer dan pastikan tombol back tetap disabled
            timeline.play();
            currentMatch.setTimerRunning(true);
            btnTimerControl.setText("⏸ PAUSE");
            setGameActive(true);
            btnBack.setDisable(true); 
        }
    }
    
    // Dipanggil setiap detik oleh Timeline untuk update waktu (countdown game atau countup break time)
    private void updateTimer() {
        if (isBreakTime) {
            // Waktu istirahat: hitung maju dan tampilkan format BREAK
            breakSecondsCounter++;
            int m = breakSecondsCounter / 60;
            int s = breakSecondsCounter % 60;
            
            lblTimer.setText(String.format("BREAK\n%02d:%02d", m, s));
            lblTimer.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #95a5a6;"); 
            
        } else {
            // Waktu game: hitung mundur dari remaining seconds
            int timeLeft = currentMatch.getRemainingSeconds();
            
            if (timeLeft == 10) {
                SoundHelper.playBuzzer();
                lblTimer.setStyle("-fx-text-fill: red; -fx-font-size: 48px; -fx-font-weight: bold;");
            }
            if (timeLeft > 0) {
                currentMatch.setRemainingSeconds(timeLeft - 1);
                updateClockDisplay();
            } else {
                // Waktu habis, masuk mode istirahat
                enterBreakMode();
            }
        }
    }

    // Masuk mode istirahat setelah waktu quarter habis
    private void enterBreakMode() {
        isBreakTime = true;
        breakSecondsCounter = 0;
        
        currentMatch.setTimerRunning(false); 
        setGameActive(false); 
        
        java.awt.Toolkit.getDefaultToolkit().beep();
        
        btnTimerControl.setDisable(true);
        btnTimerControl.setText("ISTIRAHAT");
        
        checkQuarterStatus();
    }

    // Mengecek status quarter untuk menentukan tombol selanjutnya (Next Quarter, Overtime, atau Finish)
    private void checkQuarterStatus() {
        int q = currentMatch.getCurrentQuarter();

        if (q < 4) {
            // Quarter 1-3 selesai: lanjut ke quarter berikutnya
            btnNextQuarter.setDisable(false);
            lblQuarter.setText("END Q" + q);
        } else {
            // Quarter 4 atau OT selesai: cek apakah seri atau ada pemenang
            if (currentMatch.getHomeScore() == currentMatch.getAwayScore()) {
                // Seri: lanjut ke overtime
                lblQuarter.setText("TIED");
                btnNextQuarter.setText("START OT");
                btnNextQuarter.setDisable(false);
            } else {
                // Ada pemenang: game over, tampilkan tombol finish
                lblQuarter.setText("FINAL");
                btnNextQuarter.setDisable(true);
                btnFinish.setDisable(false);
                
                timeline.stop();
                saveMatchState();
            }
        }
    }

    // Memulai quarter berikutnya atau overtime setelah break time
    @FXML
    private void handleNextQuarter() {
        isBreakTime = false;
        timeline.stop(); 
        
        // Naikkan quarter dan set waktu baru (overtime duration jika quarter > 4)
        int nextQ = currentMatch.getCurrentQuarter() + 1;
        currentMatch.setCurrentQuarter(nextQ);
        
        if (nextQ > 4) {
            currentMatch.setRemainingSeconds(OVERTIME_DURATION);
        } else {
            currentMatch.setRemainingSeconds(QUARTER_DURATION);
        }
        
        // Update UI dan reset tombol
        updateQuarterLabel();
        updateClockDisplay();
        lblTimer.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");
        
        btnNextQuarter.setDisable(true);
        btnTimerControl.setDisable(false);
        btnTimerControl.setText("▶ START");
        btnTimerControl.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        
        saveMatchState();
    }

    // Handler untuk tombol score (memanggil processScore dengan poin yang sesuai)
    @FXML private void handleActionScore1() { processScore(1); }
    @FXML private void handleActionScore2() { processScore(2); }
    @FXML private void handleActionScore3() { processScore(3); }

    // Memproses penambahan skor menggunakan gameRules.calculateNewScore() dan mencatat event dengan quarter untuk breakdown skor
    private void processScore(int pointsInput) {
        if (selectedPlayer == null) return;
        SoundHelper.playScore();

        int currentScore = isHomeTeamAction ? currentMatch.getHomeScore() : currentMatch.getAwayScore();
        int newScore = gameRules.calculateNewScore(currentScore, pointsInput);
        int actualPointsAdded = newScore - currentScore;
        
        // Update skor di object Match dan database
        if (isHomeTeamAction) {
            currentMatch.setHomeScore(newScore);
            matchDao.updateScore(currentMatch.getId(), newScore, currentMatch.getAwayScore()); 
        } else {
            currentMatch.setAwayScore(newScore);
            matchDao.updateScore(currentMatch.getId(), currentMatch.getHomeScore(), newScore);
        }

        // Catat event dengan info quarter untuk breakdown skor per quarter
        if (actualPointsAdded != 0) {
            matchDao.addMatchEvent(
                currentMatch.getId(), 
                selectedPlayer.getId(), 
                "SCORE", 
                actualPointsAdded, 
                currentMatch.getCurrentQuarter()
            );
        }
        
        updateScoreBoard();
        closeOverlay();
    }
    
    // Menangani penambahan foul untuk pemain yang dipilih dan cek foul out
    @FXML
    private void handleActionFoul() {
        if (selectedPlayer != null) {
            // Catat foul dengan quarter saat ini
            matchDao.addMatchEvent(
                currentMatch.getId(), 
                selectedPlayer.getId(), 
                "FOUL", 
                1, 
                currentMatch.getCurrentQuarter()
            );
            
            // Cek apakah pemain sudah mencapai batas foul out menggunakan gameRules
            int totalFoul = matchDao.getPlayerFoulCount(currentMatch.getId(), selectedPlayer.getId());
            if (gameRules.isFoulOut(totalFoul)) {
            	AlertHelper.showWarning("PERINGATAN", selectedPlayer.getName() + " terkena Foul Out (" + totalFoul + ")!");
            }
        }
        closeOverlay();
    }

    // Debug: skip waktu ke 5 detik terakhir untuk testing
    @FXML
    private void handleDebugSkip() {
        if (!isBreakTime && !currentMatch.isFinished()) {
            currentMatch.setRemainingSeconds(13);
            updateClockDisplay();
            
            // Auto start timer jika belum berjalan
            if (timeline.getStatus() != Timeline.Status.RUNNING) {
                timeline.play();
            }
            currentMatch.setTimerRunning(true);
            
            btnTimerControl.setText("⏸ PAUSE");
            btnTimerControl.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
            setGameActive(true);
        }
    }

    // Kembali ke dashboard dan simpan state waktu terakhir ke database
    @FXML
    private void handleBack() {
        try {
            if (timeline != null) timeline.stop();
            
            // Simpan state terakhir (quarter dan sisa detik) ke database sebelum keluar
            matchDao.updateMatchState(currentMatch.getId(), 
                                      currentMatch.getCurrentQuarter(), 
                                      currentMatch.getRemainingSeconds());

            // Navigasi kembali ke dashboard menggunakan Singleton MainController
            MainController.getInstance().showDashboard(); 
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    // Menyelesaikan match, memajukan pemenang ke round berikutnya, dan membuka kunci tombol Back
    @FXML
    private void handleFinishMatch() {
        if (currentMatch.getHomeScore() == currentMatch.getAwayScore()) {
        	AlertHelper.showWarning("Error", "Skor masih Seri!");
            return;
        }

        // Finalisasi match di database dan advance winner ke round berikutnya
        matchDao.updateScore(currentMatch.getId(), currentMatch.getHomeScore(), currentMatch.getAwayScore());
        matchDao.advanceWinnerToNextRound(currentMatch);
        
        // Buka kunci tombol Back setelah match selesai
        btnBack.setDisable(false);
        btnFinish.setDisable(true);
        btnTimerControl.setDisable(true);
        
        AlertHelper.showWarning("Selesai", "Pertandingan selesai! Silakan tekan tombol 'Back to Dashboard'.");
    }

    // Helper method untuk menyimpan state waktu match ke database
    private void saveMatchState() {
        matchDao.updateMatchState(
            currentMatch.getId(), 
            currentMatch.getCurrentQuarter(), 
            currentMatch.getRemainingSeconds()
        );
    }

    // Update tampilan jam dengan format MM:SS dari remaining seconds
    private void updateClockDisplay() {
        int totalSeconds = currentMatch.getRemainingSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        lblTimer.setText(String.format("%02d:%02d", minutes, seconds));
    }
    
    // Update label quarter (Q1-Q4 atau OT1, OT2, dll)
    private void updateQuarterLabel() {
        int q = currentMatch.getCurrentQuarter();
        lblQuarter.setText(q > 4 ? "OT " + (q-4) : "Q" + q);
    }

    // Update tampilan skor di scoreboard
    private void updateScoreBoard() {
        lblHomeScore.setText(String.valueOf(currentMatch.getHomeScore()));
        lblAwayScore.setText(String.valueOf(currentMatch.getAwayScore()));
    }

    // Mengatur aktivasi grid pemain (enable/disable dan opacity) berdasarkan status game
    private void setGameActive(boolean isActive) {
        fpHomePlayers.setDisable(!isActive);
        fpAwayPlayers.setDisable(!isActive);
        double opacity = isActive ? 1.0 : 0.5;
        fpHomePlayers.setOpacity(opacity);
        fpAwayPlayers.setOpacity(opacity);
    }

    // Memuat daftar pemain ke grid dengan tombol yang bisa diklik
    private void loadPlayersToGrid(int teamId, FlowPane grid, boolean isHome) {
        grid.getChildren().clear();
        List<Player> players = playerDao.getPlayersByTeam(teamId);
        
        for (Player p : players) {
            Button btn = new Button(p.getJerseyNumber() + "\n" + p.getName());
            btn.setPrefSize(100, 80);
            btn.setWrapText(true);
            
            // Warna berbeda untuk tim home (biru) dan away (merah)
            String color = isHome ? "#3498db" : "#e74c3c";
            btn.setStyle("-fx-base: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
            
            btn.setOnAction(e -> openActionOverlay(p, isHome));
            grid.getChildren().add(btn);
        }
    }

    // Membuka overlay aksi untuk pemain yang dipilih
    private void openActionOverlay(Player p, boolean isHome) {
        this.selectedPlayer = p;
        this.isHomeTeamAction = isHome;
        lblSelectedPlayer.setText(p.getName() + " (#" + p.getJerseyNumber() + ")");
        actionOverlay.setVisible(true);
    }
    
    // Menutup overlay aksi dan reset selected player
    @FXML private void closeOverlay() { 
        actionOverlay.setVisible(false); 
        selectedPlayer = null;
    }
    
    // Mengatur visibilitas tombol score berdasarkan aturan olahraga (Polymorphism)
    private void configureButtonsByRules() {
        List<Integer> validPoints = gameRules.getValidPointOptions();
        updateButtonVisibility(btnScore1, validPoints.contains(1));
        updateButtonVisibility(btnScore2, validPoints.contains(2));
        updateButtonVisibility(btnScore3, validPoints.contains(3));
    }

    // Helper method untuk mengatur visibilitas tombol
    private void updateButtonVisibility(Button btn, boolean isVisible) {
        btn.setVisible(isVisible);
        btn.setManaged(isVisible);
    }
}