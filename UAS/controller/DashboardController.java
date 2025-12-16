package controller;

import dao.MatchDao;
import dao.TournamentDao;
import model.Match;
import model.Tournament;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private ComboBox<Tournament> cbTournament; 
    @FXML private Label lblSportType;
    @FXML private Label lblTotalMatches;
    @FXML private HBox bracketContainer;

    private MatchDao matchDao;
    private TournamentDao tournamentDao;

    @FXML
    public void initialize() {
        matchDao = new MatchDao();
        tournamentDao = new TournamentDao();
        
        loadTournamentsToComboBox();
    }

    private void loadTournamentsToComboBox() {
        List<Tournament> tournaments = tournamentDao.getAll();
        
        if (tournaments.isEmpty()) {
            cbTournament.setPromptText("Tidak ada data");
            bracketContainer.getChildren().add(new Label("Belum ada turnamen."));
            return;
        }

        cbTournament.setItems(FXCollections.observableArrayList(tournaments));

        cbTournament.setOnAction(e -> {
            Tournament selected = cbTournament.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadDashboardData(selected.getId());
            }
        });

        cbTournament.getSelectionModel().selectFirst();
        loadDashboardData(tournaments.get(0).getId());
    }

    private void loadDashboardData(int tournamentId) {
        Tournament selected = cbTournament.getSelectionModel().getSelectedItem();
        String sportName = "Tournament Mode";
        
        if (selected != null) {
            if (selected.getSportId() == 1) sportName = "Basket";
            if (selected.getSportId() == 2) sportName = "Badminton";
        }
        
        lblSportType.setText(sportName); 

        List<Match> matches = matchDao.getMatchesByTournament(tournamentId);
        lblTotalMatches.setText(String.valueOf(matches.size()));
        
        drawVisualBracket(matches);
    }

    private void drawVisualBracket(List<Match> matches) {
        bracketContainer.getChildren().clear();

        if (matches.isEmpty()) {
            Label emptyLbl = new Label("Jadwal kosong (Generate di menu Turnamen Baru).");
            emptyLbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            bracketContainer.getChildren().add(emptyLbl);
            return;
        }

        // Grouping Match berdasarkan Round
        Map<Integer, List<Match>> matchesByRound = matches.stream()
                .collect(Collectors.groupingBy(Match::getRoundNumber));

        // Sort Round dari Besar (Quarter) ke Kecil (Final)
        List<Integer> rounds = matchesByRound.keySet().stream()
                .sorted((a, b) -> b - a) 
                .collect(Collectors.toList());

        for (int i = 0; i < rounds.size(); i++) {
            Integer round = rounds.get(i);
            List<Match> roundMatches = matchesByRound.get(round);
            
            // Sort match index agar urut dari atas ke bawah
            roundMatches.sort((m1, m2) -> Integer.compare(m1.getBracketIndex(), m2.getBracketIndex()));

            VBox roundColumn = new VBox();
            roundColumn.setAlignment(Pos.CENTER);
            
            // Spacing dinamis: Semakin ke kanan (babak akhir), jarak antar kartu semakin jauh
            double spacing = 20 + (Math.pow(2, i) * 15); 
            roundColumn.setSpacing(spacing); 
            roundColumn.setPadding(new Insets(20, 0, 20, 0));

            // Header Babak
            Label lblRoundName = new Label(getRoundName(round));
            lblRoundName.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 0 0 10 0; -fx-font-size: 14px;");
            
            // Container untuk kartu-kartu
            VBox cardsContainer = new VBox(spacing);
            cardsContainer.setAlignment(Pos.CENTER);

            for (Match m : roundMatches) {
                cardsContainer.getChildren().add(createMatchCard(m));
            }

            // Gabungkan Header + Kolom Kartu
            VBox columnWithHeader = new VBox(15);
            columnWithHeader.setAlignment(Pos.TOP_CENTER);
            columnWithHeader.getChildren().addAll(lblRoundName, cardsContainer);

            bracketContainer.getChildren().add(columnWithHeader);
            
            // Gambar Konektor (Panah)
            if (i < rounds.size() - 1) {
                VBox connectorColumn = new VBox();
                connectorColumn.setAlignment(Pos.CENTER);
                
                // Membuat garis visual sederhana menggunakan Region atau Label
                Label arrow = new Label("❯"); 
                arrow.setStyle("-fx-font-size: 24px; -fx-text-fill: #bdc3c7; -fx-font-weight: bold; -fx-opacity: 0.6;");
                
                connectorColumn.getChildren().add(arrow);
                bracketContainer.getChildren().add(connectorColumn);
            }
        }
    }

    private String getRoundName(int round) {
        switch (round) {
            case 1: return "CHAMPIONSHIP";
            case 2: return "SEMI FINAL";
            case 4: return "QUARTER FINAL";
            case 8: return "ROUND OF 16";
            default: return "ROUND " + round;
        }
    }

    // --- BAGIAN INI YANG DIPERBAGUS VISUALNYA ---
    private VBox createMatchCard(Match m) {
        VBox card = new VBox(0); // Spacing 0 karena kita pakai padding internal
        card.setMinWidth(220);
        card.setMaxWidth(220);
        
        // Style Dasar Kartu: Putih, Radius 10, Shadow Lembut
        String defaultStyle = "-fx-background-color: white; " +
                              "-fx-background-radius: 12; " +
                              "-fx-border-radius: 12; " +
                              "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 4);";
        
        // Style Saat Hover: Border Biru & Shadow Lebih Kuat
        String hoverStyle = "-fx-background-color: white; " +
                            "-fx-background-radius: 12; " +
                            "-fx-border-radius: 12; " +
                            "-fx-border-color: #375999; -fx-border-width: 2; " +
                            "-fx-effect: dropshadow(three-pass-box, rgba(55, 89, 153, 0.2), 12, 0, 0, 6); -fx-cursor: hand;";
        
        card.setStyle(defaultStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(defaultStyle));
        
        // Header Kartu (Match ID) - Warna Abu Muda
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(8, 12, 4, 12));
        Label lblInfo = new Label("Match #" + m.getBracketIndex());
        lblInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6; -fx-font-weight: bold;");
        
        // Status Label (Selesai/Live)
        Label lblStatus = new Label(m.isFinished() ? "FINAL" : "LIVE");
        lblStatus.setStyle("-fx-font-size: 9px; -fx-text-fill: " + (m.isFinished() ? "#27ae60" : "#e67e22") + "; -fx-font-weight: bold;");
        
        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);
        headerBox.getChildren().addAll(lblInfo, spacerHeader, lblStatus);

        // Baris Tim Home
        HBox boxHome = createTeamRow(m.getHomeTeamName(), m.getHomeScore(), 
                                     m.isFinished() && m.getHomeScore() > m.getAwayScore(), // Is Winner?
                                     true); // Is Top Row?

        // Baris Tim Away
        HBox boxAway = createTeamRow(m.getAwayTeamName(), m.getAwayScore(), 
                                     m.isFinished() && m.getAwayScore() > m.getHomeScore(), // Is Winner?
                                     false); // Is Top Row?

        // Navigasi saat diklik
        card.setOnMouseClicked(e -> openMatchOperator(m));

        card.getChildren().addAll(headerBox, boxHome, boxAway);
        
        // Padding bawah sedikit agar tidak mepet
        VBox.setMargin(boxAway, new Insets(0, 0, 8, 0));
        
        return card;
    }
    
    // Helper untuk membuat baris tim yang cantik
    private HBox createTeamRow(String name, int score, boolean isWinner, boolean isTopRow) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(8, 12, 8, 12)); // Padding dalam baris
        
        // Background logic: Jika menang, beri highlight hijau sangat muda
        if (isWinner) {
            box.setStyle("-fx-background-color: #f0f9f4; -fx-border-color: transparent transparent #ecf0f1 transparent;"); // Hijau pudar
        } else {
            box.setStyle("-fx-background-color: transparent; -fx-border-color: transparent transparent #ecf0f1 transparent;");
        }
        
        // Indikator Warna (Strip Kiri)
        Region indicator = new Region();
        indicator.setPrefSize(4, 16);
        indicator.setStyle("-fx-background-color: " + (isWinner ? "#2ecc71" : "#bdc3c7") + "; -fx-background-radius: 2;");

        // Nama Tim
        Label lblName = new Label(name == null ? "TBD" : name);
        String nameStyle = "-fx-font-size: 13px; -fx-text-fill: #2c3e50;";
        if (isWinner) nameStyle += " -fx-font-weight: bold;";
        lblName.setStyle(nameStyle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Skor
        Label lblScore = new Label(String.valueOf(score));
        String scoreStyle = "-fx-font-size: 14px; -fx-padding: 2 8; -fx-background-radius: 4;";
        
        if (isWinner) {
            // Skor Hijau Tebal jika menang
            scoreStyle += "-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-background-color: rgba(46, 204, 113, 0.1);";
        } else {
            // Skor Abu biasa jika kalah/belum main
            scoreStyle += "-fx-text-fill: #7f8c8d;";
        }
        lblScore.setStyle(scoreStyle);
        
        box.getChildren().addAll(indicator, lblName, spacer, lblScore);
        return box;
    }

    // Refactor Navigasi agar kode lebih bersih
    private void openMatchOperator(Match m) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MatchOperatorView.fxml"));
            Parent operatorView = loader.load();

            MatchOperatorController controller = loader.getController();
            
            Tournament selectedTournament = cbTournament.getSelectionModel().getSelectedItem();
            String sportName = "Basket"; // Default
            
            if (selectedTournament != null) {
                if (selectedTournament.getSportId() == 1) sportName = "Basket";
                if (selectedTournament.getSportId() == 2) sportName = "Badminton";
            }
            
            controller.setMatchData(m, sportName); 

            StackPane contentArea = (StackPane) bracketContainer.getScene().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(operatorView);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}