package controller;

import dao.TournamentDao;
import model.Match;
import model.Tournament;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    // --- SINGLETON PATTERN ---
    private static MainController instance;

    public static MainController getInstance() {
        return instance;
    }
    // -------------------------

    @FXML private StackPane contentArea;
    
    @FXML private Button btnDashboard;
    @FXML private Button btnNewTournament;
    @FXML private Button btnTeam;
    @FXML private Button btnHistory;

    @FXML
    public void initialize() {
        instance = this; // Set instance saat aplikasi mulai
        showDashboard(); // Default view
    }

    // --- METHOD NAVIGASI MENU ---
    
    @FXML 
    public void showDashboard() {
        loadView("/view/DashboardView.fxml");
        setActiveButton(btnDashboard); 
    }

    @FXML 
    public void showNewTournament() {
        loadView("/view/NewTournamentView.fxml");
        setActiveButton(btnNewTournament); 
    }

    @FXML 
    public void showTeamData() {
        loadView("/view/TeamView.fxml");
        setActiveButton(btnTeam); 
    }

    @FXML 
    public void showHistory() {
        loadView("/view/HistoryView.fxml");
        setActiveButton(btnHistory); 
    }

    // --- METHOD BARU: BUKA MATCH OPERATOR ---
    // Ini method yang dicari oleh TournamentController
    public void openMatchOperator(Match match) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MatchOperatorView.fxml"));
            Parent view = loader.load();

            // 1. Ambil Controllernya
            MatchOperatorController controller = loader.getController();
            
            // 2. Tentukan Jenis Olahraga (Basket/Badminton) berdasarkan ID Turnamen di Match
            String sportName = "Basket"; // Default
            TournamentDao tournamentDao = new TournamentDao();
            
            // Ambil data turnamen dari database berdasarkan match.getTournamentId()
            // Pastikan MatchDao/Model Match Anda memiliki method getTournamentId()
            Tournament t = tournamentDao.get(match.getTournamentId());
            
            if (t != null) {
                if (t.getSportId() == 1) sportName = "Basket";
                if (t.getSportId() == 2) sportName = "Badminton";
            }

            // 3. Kirim Data ke Operator
            controller.setMatchData(match, sportName);

            // 4. Ganti Tampilan Utama
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            // Matikan highlight tombol menu karena sedang tidak di menu utama
            setActiveButton(null);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal membuka Match Operator: " + e.getMessage());
        }
    }

    // --- LOGIKA TOMBOL AKTIF & LOAD VIEW ---
    
    private void setActiveButton(Button activeButton) {
        // Reset semua
        if(btnDashboard != null) btnDashboard.getStyleClass().remove("active");
        if(btnNewTournament != null) btnNewTournament.getStyleClass().remove("active");
        if(btnTeam != null) btnTeam.getStyleClass().remove("active");
        if(btnHistory != null) btnHistory.getStyleClass().remove("active");

        // Set yang baru
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }

    public void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal load view: " + fxmlPath);
        }
    }
}