package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    
    // 1. Deklarasi Button dari FXML
    @FXML private Button btnDashboard;
    @FXML private Button btnNewTournament;
    @FXML private Button btnTeam;
    @FXML private Button btnHistory;

    @FXML
    public void initialize() {
        // Set default view ke Dashboard
        showDashboard();
    }

    @FXML private void showDashboard() {
        loadView("/view/DashboardView.fxml");
        updateActiveButton(btnDashboard); // Update warna tombol
    }

    @FXML private void showNewTournament() {
        loadView("/view/NewTournamentView.fxml");
        updateActiveButton(btnNewTournament); // Update warna tombol
    }

    @FXML private void showTeamData() {
        loadView("/view/TeamView.fxml");
        updateActiveButton(btnTeam); // Update warna tombol
    }

    @FXML private void showHistory() {
        System.out.println("Buka Menu History");
        // loadView("/view/HistoryView.fxml"); // Jika sudah ada
        updateActiveButton(btnHistory); // Update warna tombol
    }

    // 2. Method Helper untuk Mengatur Style Tombol
    private void updateActiveButton(Button activeButton) {
        // Hapus class 'nav-button-active' dari SEMUA tombol
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnNewTournament.getStyleClass().remove("nav-button-active");
        btnTeam.getStyleClass().remove("nav-button-active");
        btnHistory.getStyleClass().remove("nav-button-active");

        // Tambahkan class 'nav-button-active' HANYA ke tombol yang diklik
        activeButton.getStyleClass().add("nav-button-active");
    }

    private void loadView(String fxmlPath) {
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