package Modul_06.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/Modul_06/View/MainView.fxml"));
		Scene scene = new Scene(root);
		
		primaryStage.setTitle("Data Mahasiswa");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}