package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;
    
    // Import Button
    @FXML private Button btnDashboard;
    @FXML private Button btnNewTournament;
    @FXML private Button btnTeam;
    @FXML private Button btnHistory;

    @FXML
    public void initialize() {
        // Default buka Dashboard
        showDashboard();
    }

    @FXML private void showDashboard() {
        loadView("/view/DashboardView.fxml");
        setActiveButton(btnDashboard); // Set tombol aktif
    }

    @FXML private void showNewTournament() {
        loadView("/view/NewTournamentView.fxml");
        setActiveButton(btnNewTournament); // Set tombol aktif
    }

    @FXML private void showTeamData() {
        loadView("/view/TeamView.fxml");
        setActiveButton(btnTeam); // Set tombol aktif
    }

    @FXML private void showHistory() {
        loadView("/view/HistoryView.fxml");
        setActiveButton(btnHistory); // Set tombol aktif
    }

    // --- LOGIKA TOMBOL AKTIF ---
    private void setActiveButton(Button activeButton) {
        // 1. Hapus class "active" dari SEMUA tombol
        btnDashboard.getStyleClass().remove("active");
        btnNewTournament.getStyleClass().remove("active");
        btnTeam.getStyleClass().remove("active");
        btnHistory.getStyleClass().remove("active");

        // 2. Tambahkan class "active" ke tombol yang BARU DIKLIK
        // Class "active" ini yang kita definisikan warnanya di CSS tadi
        activeButton.getStyleClass().add("active");
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