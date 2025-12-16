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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Controller mengatur logika tampilan Dashboard dan menghubungkan View dengan Model melalui DAO
public class DashboardController {
    // Komponen UI yang di-bind dari DashboardView.fxml menggunakan @FXML annotation
    @FXML private ComboBox<Tournament> cbTournament; 
    @FXML private Label lblSportType;
    @FXML private Label lblTotalMatches;
    @FXML private HBox bracketContainer;

    // Controller menggunakan DAO untuk mengakses database
    private MatchDao matchDao;
    private TournamentDao tournamentDao;

    // Konstanta dimensi layout untuk menghindari magic numbers
    private static final double CELL_WIDTH = 28.0; 
    private static final double TOTAL_WIDTH = 35.0;

    // Inisialisasi komponen DAO dan memuat data turnamen ke ComboBox
    @FXML
    public void initialize() {
        matchDao = new MatchDao();
        tournamentDao = new TournamentDao();
        loadTournamentsToComboBox();
    }

    // Memuat daftar turnamen dari database dan menampilkannya di ComboBox
    private void loadTournamentsToComboBox() {
        List<Tournament> tournaments = tournamentDao.getAll();
        
        // Validasi data kosong untuk menghindari nested if
        if (tournaments.isEmpty()) {
            cbTournament.setPromptText("Tidak ada data");
            bracketContainer.getChildren().add(new Label("Belum ada turnamen."));
            return;
        }

        // Convert List ke ObservableList untuk auto-update UI saat data berubah
        cbTournament.setItems(FXCollections.observableArrayList(tournaments));

        // Menangani event saat user memilih turnamen menggunakan lambda expression
        cbTournament.setOnAction(e -> {
            Tournament selected = cbTournament.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadDashboardData(selected.getId());
            }
        });

        // Auto-select turnamen pertama dan load datanya
        cbTournament.getSelectionModel().selectFirst();
        loadDashboardData(tournaments.get(0).getId());
    }

    // Memuat dan menampilkan data dashboard untuk turnamen tertentu
    private void loadDashboardData(int tournamentId) {
        Tournament selected = cbTournament.getSelectionModel().getSelectedItem();
        String sportName = "Tournament Mode";
        
        // Mapping sportId ke nama olahraga
        if (selected != null) {
            if (selected.getSportId() == 1) sportName = "Basket";
            if (selected.getSportId() == 2) sportName = "Badminton";
        }
        
        lblSportType.setText(sportName); 

        List<Match> matches = matchDao.getMatchesByTournament(tournamentId);
        lblTotalMatches.setText(String.valueOf(matches.size()));
        
        drawVisualBracket(matches);
    }

    // Membuat visualisasi bracket turnamen secara dinamis menggunakan Stream API
    private void drawVisualBracket(List<Match> matches) {
        bracketContainer.getChildren().clear();

        if (matches.isEmpty()) {
            Label emptyLbl = new Label("Jadwal kosong (Generate di menu Turnamen Baru).");
            emptyLbl.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            bracketContainer.getChildren().add(emptyLbl);
            return;
        }

        // Group match berdasarkan round number menggunakan Stream API dan Collectors.groupingBy
        Map<Integer, List<Match>> matchesByRound = matches.stream()
                .collect(Collectors.groupingBy(Match::getRoundNumber));

        // Sort round dari terbesar ke terkecil (Final -> Semi -> Quarter) untuk tampilan dari kanan ke kiri
        List<Integer> rounds = matchesByRound.keySet().stream()
                .sorted((a, b) -> b - a) 
                .collect(Collectors.toList());

        // Membuat kolom bracket untuk setiap round
        for (int i = 0; i < rounds.size(); i++) {
            Integer round = rounds.get(i);
            List<Match> roundMatches = matchesByRound.get(round);
            
            // Sort match dalam round berdasarkan bracket index untuk urutan yang benar
            roundMatches.sort((m1, m2) -> Integer.compare(m1.getBracketIndex(), m2.getBracketIndex()));

            VBox roundColumn = new VBox();
            roundColumn.setAlignment(Pos.CENTER);
            
            // Spacing dinamis berdasarkan posisi round (semakin akhir semakin besar untuk visualisasi yang rapi)
            double spacing = 20 + (Math.pow(2, i) * 15); 
            roundColumn.setSpacing(spacing); 
            roundColumn.setPadding(new Insets(20, 0, 20, 0));

            // Label nama round (CHAMPIONSHIP, SEMI FINAL, dll)
            Label lblRoundName = new Label(getRoundName(round));
            lblRoundName.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-padding: 0 0 10 0; -fx-font-size: 14px;");
            
            // Container untuk kartu match dalam round ini
            VBox cardsContainer = new VBox(spacing);
            cardsContainer.setAlignment(Pos.CENTER);

            // Membuat kartu match untuk setiap match dalam round
            for (Match m : roundMatches) {
                cardsContainer.getChildren().add(createMatchCard(m));
            }

            VBox columnWithHeader = new VBox(15);
            columnWithHeader.setAlignment(Pos.TOP_CENTER);
            columnWithHeader.getChildren().addAll(lblRoundName, cardsContainer);

            bracketContainer.getChildren().add(columnWithHeader);
            
            // Tambahkan connector arrow antar round
            if (i < rounds.size() - 1) {
                VBox connectorColumn = new VBox();
                connectorColumn.setAlignment(Pos.CENTER);
                Label arrow = new Label("❯"); 
                arrow.setStyle("-fx-font-size: 24px; -fx-text-fill: #bdc3c7; -fx-font-weight: bold; -fx-opacity: 0.6;");
                connectorColumn.getChildren().add(arrow);
                bracketContainer.getChildren().add(connectorColumn);
            }
        }
    }

    // Mengkonversi nomor round menjadi nama yang user-friendly
    private String getRoundName(int round) {
        switch (round) {
            case 1: return "CHAMPIONSHIP";
            case 2: return "SEMI FINAL";
            case 4: return "QUARTER FINAL";
            case 8: return "ROUND OF 16";
            default: return "ROUND " + round;
        }
    }

    // Membuat kartu visual untuk satu match dengan informasi lengkap (skor per quarter, total, pemenang)
    private VBox createMatchCard(Match m) {
        VBox card = new VBox(0); 
        card.setMinWidth(280);
        card.setMaxWidth(280);
        
        // Styling untuk kartu: default dan hover effect untuk interaktivitas
        String defaultStyle = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);";
        String hoverStyle = "-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #375999; -fx-border-width: 2; -fx-effect: dropshadow(three-pass-box, rgba(55, 89, 153, 0.2), 8, 0, 0, 4); -fx-cursor: hand;";
        
        card.setStyle(defaultStyle);
        // Event handler untuk hover effect (ubah style saat mouse masuk/keluar)
        card.setOnMouseEntered(e -> card.setStyle(hoverStyle));
        card.setOnMouseExited(e -> card.setStyle(defaultStyle));
        
        // Memuat data skor per quarter dari database jika match sudah dimulai
        Map<Integer, Integer> homeScores = Collections.emptyMap();
        Map<Integer, Integer> awayScores = Collections.emptyMap();

        // Hanya load quarter scores jika match sudah ada di database dan memiliki skor
        if (m.getId() != 0 && (m.getHomeScore() > 0 || m.getAwayScore() > 0)) {
            Map<Integer, Map<Integer, Integer>> qScores = matchDao.getQuarterScores(m.getId());
            if (qScores.containsKey(m.getHomeTeamId())) homeScores = qScores.get(m.getHomeTeamId());
            if (qScores.containsKey(m.getAwayTeamId())) awayScores = qScores.get(m.getAwayTeamId());
        }

        // Mengecek apakah match memiliki overtime (quarter > 4) menggunakan Stream API anyMatch
        boolean hasOT = homeScores.keySet().stream().anyMatch(q -> q > 4) || 
                        awayScores.keySet().stream().anyMatch(q -> q > 4);

        // Header kartu: Match Index di kiri, Header Score (Q1 Q2 Q3 Q4 [OT] T) di kanan
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(8, 12, 4, 12));
        
        // Kiri: Match Index (Match #1, Match #2, dll)
        Label lblInfo = new Label("Match #" + m.getBracketIndex());
        lblInfo.setStyle("-fx-font-size: 10px; -fx-text-fill: #95a5a6; -fx-font-weight: bold;");
        
        // Spacer untuk mendorong header score ke kanan
        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);
        
        // Kanan: Header Score (Q1 Q2 Q3 Q4 [OT] T)
        HBox scoreHeader = createScoreHeaderRow(hasOT);
        
        headerBox.getChildren().addAll(lblInfo, spacerHeader, scoreHeader);

        // Baris untuk tim home (menentukan pemenang dengan membandingkan skor)
        HBox boxHome = createTeamRow(m.getHomeTeamName(), m.getHomeScore(), 
                                     m.isFinished() && m.getHomeScore() > m.getAwayScore(), 
                                     homeScores, hasOT);

        // Baris untuk tim away (menentukan pemenang dengan membandingkan skor)
        HBox boxAway = createTeamRow(m.getAwayTeamName(), m.getAwayScore(), 
                                     m.isFinished() && m.getAwayScore() > m.getHomeScore(), 
                                     awayScores, hasOT);

        // Event handler: klik kartu untuk membuka Match Operator
        card.setOnMouseClicked(e -> openMatchOperator(m));
        card.getChildren().addAll(headerBox, boxHome, boxAway);
        
        // Margin bawah sedikit
        VBox.setMargin(boxAway, new Insets(0, 0, 8, 0));
        
        return card;
    }

    // Membuat baris header untuk menampilkan label Q1, Q2, Q3, Q4, OT, T
    private HBox createScoreHeaderRow(boolean hasOT) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_RIGHT);
        
        String style = "-fx-font-size: 9px; -fx-text-fill: #bdc3c7; -fx-font-weight: bold; -fx-alignment: CENTER;";
        
        row.getChildren().add(createFixedLabel("Q1", CELL_WIDTH, style));
        row.getChildren().add(createFixedLabel("Q2", CELL_WIDTH, style));
        row.getChildren().add(createFixedLabel("Q3", CELL_WIDTH, style));
        row.getChildren().add(createFixedLabel("Q4", CELL_WIDTH, style));
        
        if (hasOT) {
            row.getChildren().add(createFixedLabel("OT", CELL_WIDTH, style));
        }
        
        // Kolom Total
        row.getChildren().add(createFixedLabel("T", TOTAL_WIDTH, style));
        
        return row;
    }

    // Membuat baris untuk menampilkan informasi satu tim (nama, skor per quarter, total)
    private HBox createTeamRow(String name, int totalScore, boolean isWinner, Map<Integer, Integer> scores, boolean hasOT) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(4, 12, 4, 12));
        
        // Background hijau untuk pemenang, transparan untuk yang kalah
        if (isWinner) {
            row.setStyle("-fx-background-color: #f0f9f4;");
        } else {
            row.setStyle("-fx-background-color: transparent;");
        }
        
        // Indikator warna: hijau untuk pemenang, abu-abu untuk yang kalah
        Region indicator = new Region();
        indicator.setPrefSize(4, 16);
        indicator.setStyle("-fx-background-color: " + (isWinner ? "#2ecc71" : "#bdc3c7") + "; -fx-background-radius: 2;");
        HBox.setMargin(indicator, new Insets(0, 8, 0, 0));

        // Nama tim dengan styling bold untuk pemenang
        Label lblName = new Label(name == null ? "TBD" : name);
        String nameStyle = "-fx-font-size: 12px; -fx-text-fill: #2c3e50;";
        if (isWinner) nameStyle += " -fx-font-weight: bold;";
        lblName.setStyle(nameStyle);
        
        // Spacer untuk mendorong skor ke kanan
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Container untuk skor per quarter (Q1, Q2, Q3, Q4, OT, Total)
        HBox scoreValues = new HBox(0);
        scoreValues.setAlignment(Pos.CENTER_RIGHT);
        
        String valStyle = "-fx-font-size: 11px; -fx-text-fill: #34495e; -fx-alignment: CENTER;";
        
        // Menampilkan skor untuk setiap quarter (Q1-Q4)
        scoreValues.getChildren().add(createFixedLabel(getScore(scores, 1), CELL_WIDTH, valStyle));
        scoreValues.getChildren().add(createFixedLabel(getScore(scores, 2), CELL_WIDTH, valStyle));
        scoreValues.getChildren().add(createFixedLabel(getScore(scores, 3), CELL_WIDTH, valStyle));
        scoreValues.getChildren().add(createFixedLabel(getScore(scores, 4), CELL_WIDTH, valStyle));
        
        // Jika ada overtime, hitung total poin di overtime (quarter > 4) menggunakan Stream API
        if (hasOT) {
            int otVal = scores.entrySet().stream()
                              .filter(e -> e.getKey() > 4)
                              .mapToInt(Map.Entry::getValue).sum();
            scoreValues.getChildren().add(createFixedLabel(String.valueOf(otVal), CELL_WIDTH, valStyle));
        }
        
        // Total skor dengan styling berbeda untuk pemenang (hijau) dan yang kalah (abu-abu)
        String totalStyle = "-fx-font-size: 12px; -fx-alignment: CENTER; -fx-font-weight: bold;";
        totalStyle += isWinner ? " -fx-text-fill: #27ae60;" : " -fx-text-fill: #7f8c8d;";
        
        scoreValues.getChildren().add(createFixedLabel(String.valueOf(totalScore), TOTAL_WIDTH, totalStyle));

        // Gabung semua ke Row
        row.getChildren().addAll(indicator, lblName, spacer, scoreValues);
        
        return row;
    }
    
    // Membuat Label dengan lebar tetap untuk menjaga alignment kolom skor
    private Label createFixedLabel(String text, double width, String style) {
        Label lbl = new Label(text);
        lbl.setMinWidth(width);
        lbl.setMaxWidth(width);
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle(style);
        return lbl;
    }
    
    // Mengambil skor quarter dengan null safety menggunakan getOrDefault
    private String getScore(Map<Integer, Integer> scores, int q) {
        if (scores == null || scores.isEmpty()) return "-";
        return String.valueOf(scores.getOrDefault(q, 0));
    }

    // Membuka halaman Match Operator untuk mengoperasikan match tertentu
    private void openMatchOperator(Match m) {
        try {
            // Load FXML view dan controller menggunakan FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MatchOperatorView.fxml"));
            Parent operatorView = loader.load();

            // Ambil controller dari loader untuk mengirim data
            MatchOperatorController controller = loader.getController();
            
            // Tentukan nama olahraga berdasarkan sportId untuk menentukan strategi scoring
            Tournament selectedTournament = cbTournament.getSelectionModel().getSelectedItem();
            String sportName = "Basket"; 
            
            if (selectedTournament != null) {
                if (selectedTournament.getSportId() == 1) sportName = "Basket";
                if (selectedTournament.getSportId() == 2) sportName = "Badminton";
            }
            
            // Kirim data match dan nama olahraga ke controller
            controller.setMatchData(m, sportName); 

            // Ganti konten area dengan view Match Operator menggunakan scene lookup
            StackPane contentArea = (StackPane) bracketContainer.getScene().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(operatorView);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}