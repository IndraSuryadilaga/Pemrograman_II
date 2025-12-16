package controller;

import dao.MatchDao;
import dao.TeamDao;
import model.Match;
import model.Team;
import util.FormatterHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Controller mengatur logika tampilan Tournament dengan live scoring dan generate bracket
public class TournamentController {
    // Komponen UI yang di-bind dari TournamentView.fxml
    @FXML private ListView<Match> listMatches;
    @FXML private Label lblHomeName, lblAwayName;
    @FXML private Label lblHomeScore, lblAwayScore;
    @FXML private Label lblStatus;
    @FXML private Button btnFinish;

    // Controller menggunakan DAO untuk mengakses database
    private MatchDao matchDao;
    private TeamDao teamDao;
    private Match currentMatch;
    
    // Variabel untuk live scoring (skor sementara sebelum di-save)
    private int currentHomeScore = 0;
    private int currentAwayScore = 0;

    // Inisialisasi komponen DAO, load data match, dan setup event listener untuk selection
    @FXML
    public void initialize() {
        matchDao = new MatchDao();
        teamDao = new TeamDao();
        
        loadMatches();

        // Event listener untuk selection change di ListView menggunakan lambda
        // Saat user memilih match, tampilkan detail match tersebut
        listMatches.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectMatch(newVal);
            }
        });
    }

    // Memuat semua match dari turnamen tertentu ke ListView menggunakan ObservableList
    private void loadMatches() {
        List<Match> matches = matchDao.getMatchesByTournament(1);
        ObservableList<Match> items = FXCollections.observableArrayList(matches);
        listMatches.setItems(items);
    }

    // Menampilkan detail match yang dipilih di scoreboard dan reset skor sementara
    private void selectMatch(Match m) {
        this.currentMatch = m;
        
        // Tampilkan nama tim di scoreboard
        lblHomeName.setText(m.getHomeTeamName());
        lblAwayName.setText(m.getAwayTeamName());
        
        // Reset skor sementara dari database untuk live scoring
        currentHomeScore = m.getHomeScore();
        currentAwayScore = m.getAwayScore();
        updateScoreDisplay();
        
        lblStatus.setText("Pertandingan dipilih: " + m.toString());
        btnFinish.setDisable(m.isFinished());
    }

    // Menangani generate bracket baru untuk turnamen menggunakan algoritma bracket generation
    @FXML
    private void handleGenerate() {
        List<Team> allTeams = teamDao.getAll();
        if (allTeams.size() < 4) {
            lblStatus.setText("Error: Butuh minimal 4 tim untuk generate!");
            return;
        }

        // Ambil ID dari semua tim untuk generate bracket
        List<Integer> ids = new ArrayList<>();
        for (Team t : allTeams) ids.add(t.getId());

        // Generate bracket menggunakan algoritma bracket generation dari DAO
        if (matchDao.generateBracket(1, ids)) {
            lblStatus.setText("Sukses Generate Bracket Baru!");
            loadMatches();
        } else {
            lblStatus.setText("Gagal generate (Mungkin sudah ada jadwal?)");
        }
    }

    // Handler untuk tombol live scoring (memanggil updateHome/updateAway dengan poin yang sesuai)
    @FXML private void addHome1() { updateHome(1); }
    @FXML private void addHome2() { updateHome(2); }
    @FXML private void addHome3() { updateHome(3); }
    
    @FXML private void addAway1() { updateAway(1); }
    @FXML private void addAway2() { updateAway(2); }
    @FXML private void addAway3() { updateAway(3); }

    // Menambahkan poin ke tim home dan update tampilan scoreboard
    private void updateHome(int points) {
        if (currentMatch == null || currentMatch.isFinished()) return;
        currentHomeScore += points;
        playAudioEffect();
        updateScoreDisplay();
    }

    // Menambahkan poin ke tim away dan update tampilan scoreboard
    private void updateAway(int points) {
        if (currentMatch == null || currentMatch.isFinished()) return;
        currentAwayScore += points;
        playAudioEffect();
        updateScoreDisplay();
    }

    // Update tampilan skor di scoreboard dengan nilai skor sementara
    private void updateScoreDisplay() {
        lblHomeScore.setText(String.valueOf(currentHomeScore));
        lblAwayScore.setText(String.valueOf(currentAwayScore));
    }

    // Memutar efek suara saat terjadi scoring
    private void playAudioEffect() {
    	System.out.println("Beeep");
    }

    // Menyelesaikan match dan menyimpan skor final ke database
    @FXML
    private void handleFinishMatch() {
        if (currentMatch == null) return;

        boolean success = matchDao.updateScore(currentMatch.getId(), currentHomeScore, currentAwayScore);
        
        if (success) {
            lblStatus.setText("Pertandingan Selesai & Disimpan!");
            loadMatches();
            btnFinish.setDisable(true);
        } else {
            lblStatus.setText("Gagal menyimpan ke database.");
        }
    }
    
    // Membuat node visual untuk satu match di ListView dengan informasi skor per quarter
    private VBox createMatchNode(Match match) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 0);");
        card.setMinWidth(160);

        // Memuat detail skor per quarter dari database jika match sudah dimulai
        String homeQDetail = "-";
        String awayQDetail = "-";
        
        if (match.getId() != 0 && (match.getHomeScore() > 0 || match.getAwayScore() > 0)) {
            Map<Integer, Map<Integer, Integer>> qScores = matchDao.getQuarterScores(match.getId());
            
            if (qScores.containsKey(match.getHomeTeamId())) {
                homeQDetail = FormatterHelper.formatScoreDetail(qScores.get(match.getHomeTeamId()));
            }
            if (qScores.containsKey(match.getAwayTeamId())) {
                awayQDetail = FormatterHelper.formatScoreDetail(qScores.get(match.getHomeTeamId()));
            }
        }

        // Display tim home dengan total skor dan detail per quarter
        Label lblHome = new Label(match.getHomeTeamName().isEmpty() ? "TBD" : match.getHomeTeamName());
        lblHome.setStyle("-fx-font-weight: bold;");
        Label lblHomeTotal = new Label(String.valueOf(match.getHomeScore()));
        lblHomeTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblHomeDetail = new Label(homeQDetail); 
        lblHomeDetail.setStyle("-fx-font-size: 9px; -fx-text-fill: grey;");

        VBox homeRight = new VBox(lblHomeTotal, lblHomeDetail);
        homeRight.setAlignment(Pos.CENTER_RIGHT);
        
        HBox homeBox = new HBox(10, lblHome, new Region(), homeRight);
        HBox.setHgrow(homeBox.getChildren().get(1), Priority.ALWAYS);
        homeBox.setAlignment(Pos.CENTER_LEFT);

        Separator sep = new Separator();

        // Display tim away dengan total skor dan detail per quarter
        Label lblAway = new Label(match.getAwayTeamName().isEmpty() ? "TBD" : match.getAwayTeamName());
        lblAway.setStyle("-fx-font-weight: bold;");
        Label lblAwayTotal = new Label(String.valueOf(match.getAwayScore()));
        lblAwayTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblAwayDetail = new Label(awayQDetail);
        lblAwayDetail.setStyle("-fx-font-size: 9px; -fx-text-fill: grey;");

        VBox awayRight = new VBox(lblAwayTotal, lblAwayDetail);
        awayRight.setAlignment(Pos.CENTER_RIGHT);
        
        HBox awayBox = new HBox(10, lblAway, new Region(), awayRight);
        HBox.setHgrow(awayBox.getChildren().get(1), Priority.ALWAYS);
        awayBox.setAlignment(Pos.CENTER_LEFT);

        // Event handler: klik kartu untuk membuka Match Operator menggunakan Singleton MainController
        if (!match.getHomeTeamName().isEmpty() && !match.getAwayTeamName().isEmpty()) {
            card.setCursor(Cursor.HAND);
            card.setOnMouseClicked(e -> controller.MainController.getInstance().openMatchOperator(match));
        }

        card.getChildren().addAll(new Label("Match #" + match.getId()), homeBox, sep, awayBox);
        return card;
    }
}