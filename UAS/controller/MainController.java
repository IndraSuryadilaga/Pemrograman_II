package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        showDashboard();
    }

    @FXML private void showDashboard() {
        loadView("/view/DashboardView.fxml");
    }

    @FXML private void showNewTournament() {
        loadView("/view/NewTournamentView.fxml");
    }

    @FXML private void showTeamData() {
        // Load view team yang lama (TeamView.fxml) tapi disesuaikan
        loadView("/view/TeamView.fxml");
    }

    @FXML private void showHistory() {
        System.out.println("Buka Menu History");
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