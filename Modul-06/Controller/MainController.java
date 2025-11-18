package Modul_06.Controller;

import Modul_06.Model.Mahasiswa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;

public class MainController {
	@FXML
	private TableView<Mahasiswa> tableView;
	
	@FXML
	private TableColumn<Mahasiswa, String> kolomNim;
	
	@FXML
	private TableColumn<Mahasiswa, String> kolomNama;
	
	private ObservableList<Mahasiswa> dataMahasiswa;
	
	@FXML
	public void initialize() {
		dataMahasiswa = FXCollections.observableArrayList(
				new Mahasiswa(1, "John", "123"),
				new Mahasiswa(2, "Jane", "123"),
				new Mahasiswa(3, "Jono", "124123"),
				new Mahasiswa(4, "Agus", "1241234"), 
				new Mahasiswa(5, "Budi", "125"),
				new Mahasiswa(6, "Citra", "126"),
				new Mahasiswa(7, "Dewi", "127"),
				new Mahasiswa(8, "Eka", "128"),
				new Mahasiswa(9, "Fajar", "129"),
				new Mahasiswa(10, "Gita", "130")
				);
		
		kolomNim.setCellValueFactory(new PropertyValueFactory<>("nim"));
		kolomNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
		
		tableView.setItems(dataMahasiswa);
	}
	
}
