package controller;

import dao.MatchDao;
import model.Match;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label lblActiveTournament;
    @FXML private Label lblSportType;
    @FXML private Label lblTotalMatches;
    @FXML private HBox bracketContainer;

    private MatchDao matchDao;

    @FXML
    public void initialize() {
        matchDao = new MatchDao();
        
        // HARDCODE ID 1 (UAS Cup 2025) untuk demo
        // Nanti bisa dibuat dinamis mengambil turnamen yang status='ONGOING'
        int activeTournamentId = 1; 
        
        loadDashboardData(activeTournamentId);
    }

    private void loadDashboardData(int tournamentId) {
        lblActiveTournament.setText("UAS Cup 2025"); // Harusnya ambil dari query Turnamen
        lblSportType.setText("Basketball");

        // 1. Ambil Data Pertandingan
        List<Match> matches = matchDao.getMatchesByTournament(tournamentId);
        lblTotalMatches.setText(String.valueOf(matches.size()));
        
        // 2. Gambar Bracket
        drawVisualBracket(matches);
    }

    private void drawVisualBracket(List<Match> matches) {
        bracketContainer.getChildren().clear();

        if (matches.isEmpty()) {
            bracketContainer.getChildren().add(new Label("Belum ada jadwal pertandingan. Silakan generate di menu Turnamen Baru."));
            return;
        }

        // A. Grouping Match berdasarkan Round Number (4=Quarter, 2=Semi, 1=Final)
        Map<Integer, List<Match>> matchesByRound = matches.stream()
                .collect(Collectors.groupingBy(Match::getRoundNumber));

        // B. Urutkan Key (Round) dari Besar ke Kecil (4 -> 2 -> 1)
        List<Integer> rounds = matchesByRound.keySet().stream()
                .sorted((a, b) -> b - a) 
                .collect(Collectors.toList());

        // C. Loop setiap Ronde untuk membuat Kolom
        for (int i = 0; i < rounds.size(); i++) {
            Integer round = rounds.get(i);
            List<Match> roundMatches = matchesByRound.get(round);

            // Sort pertandingan berdasarkan bracket_index agar urut atas-bawah
            roundMatches.sort((m1, m2) -> Integer.compare(m1.getBracketIndex(), m2.getBracketIndex()));

            // Buat Kolom Vertikal untuk ronde ini
            VBox roundColumn = new VBox();
            roundColumn.setAlignment(Pos.CENTER);
            
            // LOGIKA SPACING DINAMIS (Agar terlihat seperti Tree)
            // Semakin ke kanan (babak akhir), jarak antar kotak semakin jauh
            double spacing = Math.pow(2, i) * 20; 
            roundColumn.setSpacing(spacing); 
            
            // Tambahkan margin atas agar sejajar tengah
            roundColumn.setPadding(new javafx.geometry.Insets(spacing / 2, 0, 0, 0));

            // Tambahkan Label Judul Babak
            Label lblRoundName = new Label(getRoundName(round));
            lblRoundName.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
            // roundColumn.getChildren().add(lblRoundName); // Opsional: taruh header di atas kolom

            // D. Masukkan Kartu Match ke Kolom
            for (Match m : roundMatches) {
                roundColumn.getChildren().add(createMatchCard(m));
            }

            // E. Masukkan Kolom ke Container Utama (HBox)
            bracketContainer.getChildren().add(roundColumn);
            
            // Tambahkan Garis Penghubung (Visual Hack: Label Panah)
            if (i < rounds.size() - 1) {
                VBox connectorColumn = new VBox();
                connectorColumn.setAlignment(Pos.CENTER);
                Label arrow = new Label("  ➝  ");
                arrow.setStyle("-fx-font-size: 20px; -fx-text-fill: #bdc3c7;");
                connectorColumn.getChildren().add(arrow);
                bracketContainer.getChildren().add(connectorColumn);
            }
        }
    }

    private String getRoundName(int round) {
        switch (round) {
            case 1: return "Grand Final";
            case 2: return "Semi Final";
            case 4: return "Quarter Final";
            default: return "Round of " + round;
        }
    }

    // Method Membuat Tampilan Satu Kotak Pertandingan
    private VBox createMatchCard(Match m) {
        VBox card = new VBox(5);
        card.setMinWidth(200);
        card.setMaxWidth(200);
        
        // Styling CSS dalam kode (Inline)
        String defaultStyle = "-fx-background-color: white; -fx-border-color: #ecf0f1; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);";
        String hoverStyle = "-fx-background-color: #fdfdfd; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;";
        
        card.setStyle(defaultStyle);
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(defaultStyle));

        // Isi Kartu
        Label lblInfo = new Label(getRoundName(m.getRoundNumber()) + " - Match #" + m.getBracketIndex());
        lblInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6;");

        // Tim Home
        HBox boxHome = new HBox(10);
        Label nameHome = new Label(m.getHomeTeamName() == null ? "TBD" : m.getHomeTeamName());
        nameHome.setStyle("-fx-font-weight: bold;");
        Label scoreHome = new Label(String.valueOf(m.getHomeScore()));
        boxHome.getChildren().addAll(nameHome, new Region(), scoreHome);
        HBox.setHgrow(boxHome.getChildren().get(1), Priority.ALWAYS);

        // Tim Away
        HBox boxAway = new HBox(10);
        Label nameAway = new Label(m.getAwayTeamName() == null ? "TBD" : m.getAwayTeamName());
        nameAway.setStyle("-fx-font-weight: bold;");
        Label scoreAway = new Label(String.valueOf(m.getAwayScore()));
        boxAway.getChildren().addAll(nameAway, new Region(), scoreAway);
        HBox.setHgrow(boxAway.getChildren().get(1), Priority.ALWAYS);

        // Status Selesai
        if (m.isFinished()) {
            Label lblDone = new Label("✅ FINAL");
            lblDone.setStyle("-fx-font-size: 9px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            card.getChildren().add(lblDone);
        }

        card.getChildren().addAll(lblInfo, new Separator(), boxHome, boxAway);
        
        return card;
    }
}