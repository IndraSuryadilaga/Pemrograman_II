package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dao.MatchDao;
import dao.TeamDao;
import model.Match;
import model.Team;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/MainLayout.fxml"));
            
            Scene scene = new Scene(root);
            
            primaryStage.setTitle("SPORTA - Sistem Pertandingan Olahraga");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Gagal meload FXML. Cek path file-nya!");
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}