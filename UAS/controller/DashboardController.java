package controller;

import dao.MatchDao;
import dao.TournamentDao;
import model.Match;
import model.Tournament;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    // Ganti Label lama dengan ComboBox
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

        // Isi ComboBox
        cbTournament.setItems(FXCollections.observableArrayList(tournaments));

        // Listener: Jika user ganti pilihan, load data baru
        cbTournament.setOnAction(e -> {
            Tournament selected = cbTournament.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadDashboardData(selected.getId());
            }
        });

        // Pilih turnamen pertama (terbaru) secara otomatis
        cbTournament.getSelectionModel().selectFirst();
        loadDashboardData(tournaments.get(0).getId());
    }

    private void loadDashboardData(int tournamentId) {
        lblSportType.setText("Knockout System"); // Bisa diambil dari DB jika mau

        // 1. Ambil Data Pertandingan
        List<Match> matches = matchDao.getMatchesByTournament(tournamentId);
        lblTotalMatches.setText(String.valueOf(matches.size()));
        
        // 2. Gambar Bracket
        drawVisualBracket(matches);
    }

    private void drawVisualBracket(List<Match> matches) {
        bracketContainer.getChildren().clear();

        if (matches.isEmpty()) {
            bracketContainer.getChildren().add(new Label("Jadwal kosong (Generate di menu Turnamen Baru)."));
            return;
        }

        Map<Integer, List<Match>> matchesByRound = matches.stream()
                .collect(Collectors.groupingBy(Match::getRoundNumber));

        List<Integer> rounds = matchesByRound.keySet().stream()
                .sorted((a, b) -> b - a) 
                .collect(Collectors.toList());

        for (int i = 0; i < rounds.size(); i++) {
            Integer round = rounds.get(i);
            List<Match> roundMatches = matchesByRound.get(round);
            roundMatches.sort((m1, m2) -> Integer.compare(m1.getBracketIndex(), m2.getBracketIndex()));

            VBox roundColumn = new VBox();
            roundColumn.setAlignment(Pos.CENTER);
            double spacing = Math.pow(2, i) * 30; 
            roundColumn.setSpacing(spacing); 
            roundColumn.setPadding(new javafx.geometry.Insets(spacing / 2, 0, 0, 0));

            Label lblRoundName = new Label(getRoundName(round));
            lblRoundName.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d; -fx-padding: 0 0 15 0;");
            
            for (Match m : roundMatches) {
                roundColumn.getChildren().add(createMatchCard(m));
            }

            VBox columnWithHeader = new VBox(10);
            columnWithHeader.setAlignment(Pos.TOP_CENTER);
            columnWithHeader.getChildren().addAll(lblRoundName, roundColumn);

            bracketContainer.getChildren().add(columnWithHeader);
            
            if (i < rounds.size() - 1) {
                VBox connectorColumn = new VBox();
                connectorColumn.setAlignment(Pos.CENTER);
                Label arrow = new Label(" ➝ ");
                arrow.setStyle("-fx-font-size: 20px; -fx-text-fill: #bdc3c7; -fx-font-weight: bold;");
                connectorColumn.getChildren().add(arrow);
                bracketContainer.getChildren().add(connectorColumn);
            }
        }
    }

    private String getRoundName(int round) {
        switch (round) {
            case 1: return "CHAMPION";
            case 2: return "Grand Final"; // Biasanya round 1 itu winner, round 2 itu final (tergantung logika generate)
            case 4: return "Semi Final";
            case 8: return "Quarter Final";
            default: return "Round of " + round;
        }
    }

    private VBox createMatchCard(Match m) {
        VBox card = new VBox(5);
        card.setMinWidth(200);
        card.setMaxWidth(200);
        
        String defaultStyle = "-fx-background-color: white; -fx-border-color: #ecf0f1; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);";
        String hoverStyle = "-fx-background-color: #fdfdfd; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;";
        
        card.setStyle(defaultStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(defaultStyle));

        // FIX VISUAL: Set text fill ke warna gelap (#7f8c8d) agar terlihat di background putih
        Label lblInfo = new Label("Match #" + m.getBracketIndex());
        lblInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;"); 

        HBox boxHome = createTeamRow(m.getHomeTeamName(), m.getHomeScore(), m.getHomeScore() > m.getAwayScore() && m.isFinished());
        HBox boxAway = createTeamRow(m.getAwayTeamName(), m.getAwayScore(), m.getAwayScore() > m.getHomeScore() && m.isFinished());

        card.getChildren().addAll(lblInfo, new Separator(), boxHome, boxAway);
        return card;
    }
    
    private HBox createTeamRow(String name, int score, boolean isWinner) {
        HBox box = new HBox(10);
        
        // FIX VISUAL: Pastikan text tidak putih
        String baseTextStyle = "-fx-font-size: 13px; -fx-text-fill: #2c3e50;"; // Biru Gelap
        
        Label lblName = new Label(name == null ? "TBD" : name);
        Label lblScore = new Label(String.valueOf(score));
        
        if (isWinner) {
            // Jika menang, tebal dan hijau
            lblName.setStyle(baseTextStyle + "-fx-font-weight: bold; -fx-text-fill: #27ae60;"); 
            lblScore.setStyle(baseTextStyle + "-fx-font-weight: bold; -fx-text-fill: #27ae60;");
        } else {
            // Jika biasa, warna gelap standar
            lblName.setStyle(baseTextStyle + "-fx-font-weight: bold;");
            lblScore.setStyle(baseTextStyle);
        }
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        box.getChildren().addAll(lblName, spacer, lblScore);
        return box;
    }
}