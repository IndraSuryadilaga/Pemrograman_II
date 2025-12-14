package controller;

import dao.MatchDao;
import dao.TeamDao;
import model.Match;
import model.Team;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
}