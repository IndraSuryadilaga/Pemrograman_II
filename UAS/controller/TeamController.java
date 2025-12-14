package controller;

import dao.TeamDao;
import model.Team;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TeamController {

    // 1. Deklarasi Komponen FXML (Harus sama persis dengan fx:id di FXML)
    @FXML private TableView<Team> tableTeams;
    @FXML private TableColumn<Team, Integer> colId;
    @FXML private TableColumn<Team, String> colName;
    @FXML private TableColumn<Team, String> colLogo;
    
    @FXML private TextField tfName;
    @FXML private TextField tfLogo;
    @FXML private Button btnSave;
    @FXML private Button btnDelete;

    // 2. Variabel Pembantu
    private TeamDao teamDao;
    private ObservableList<Team> teamList; // List khusus JavaFX yang bisa auto-update UI
    private Team selectedTeam; // Untuk menyimpan tim yang sedang diklik user

    // 3. Method initialize (Dipanggil otomatis saat layar dibuka)
    @FXML
    public void initialize() {
        teamDao = new TeamDao();
        teamList = FXCollections.observableArrayList();

        // Hubungkan Kolom Tabel dengan Atribut di Class Team
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLogo.setCellValueFactory(new PropertyValueFactory<>("logoPath"));

        // Load data awal dari database
        loadData();

        // Event Listener: Jika baris tabel diklik -> Isi form
        tableTeams.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTeam = newSelection;
                tfName.setText(selectedTeam.getName());
                tfLogo.setText(selectedTeam.getLogoPath());
                btnSave.setText("Update"); // Ubah tombol jadi Update
            } else {
                clearForm();
            }
        });
    }

    private void loadData() {
        teamList.clear();
        teamList.addAll(teamDao.getAll()); // Ambil dari DAO
        tableTeams.setItems(teamList);     // Pasang ke Tabel
    }

    private void clearForm() {
        tfName.clear();
        tfLogo.clear();
        selectedTeam = null;
        btnSave.setText("Simpan");
        tableTeams.getSelectionModel().clearSelection();
    }

    // 4. Handle Button Actions
    @FXML
    private void handleSave() {
        String name = tfName.getText();
        String logo = tfLogo.getText();

        if (name.isEmpty()) {
            showAlert("Error", "Nama tim tidak boleh kosong!");
            return;
        }

        if (selectedTeam == null) {
            // Mode INSERT (Data Baru)
            Team newTeam = new Team(name, logo);
            if (teamDao.add(newTeam)) {
                showAlert("Sukses", "Data berhasil disimpan!");
                loadData();
                clearForm();
            }
        } else {
            // Mode UPDATE (Edit Data Lama)
            selectedTeam.setName(name);
            selectedTeam.setLogoPath(logo);
            if (teamDao.update(selectedTeam)) {
                showAlert("Sukses", "Data berhasil diupdate!");
                loadData();
                clearForm();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedTeam != null) {
            if (teamDao.delete(selectedTeam.getId())) {
                showAlert("Sukses", "Data berhasil dihapus!");
                loadData();
                clearForm();
            }
        } else {
            showAlert("Warning", "Pilih data di tabel dulu untuk dihapus.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}