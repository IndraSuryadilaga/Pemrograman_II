package controller;

import dao.PlayerDao;
import dao.TeamDao;
import model.Player;
import model.Team;
import util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

// Controller mengatur logika CRUD untuk Team dan Player menggunakan PropertyValueFactory dan event listener
public class TeamController {

    // Komponen UI tabel dan form untuk Team yang di-bind dari TeamView.fxml
    @FXML private TableView<Team> tableTeams;
    @FXML private TableColumn<Team, Integer> colTeamId;
    @FXML private TableColumn<Team, String> colTeamName;
    @FXML private TextField tfTeamName;

    // Komponen UI tabel dan form untuk Player yang di-bind dari TeamView.fxml
    @FXML private Label lblSelectedTeam;
    @FXML private TableView<Player> tablePlayers;
    @FXML private TableColumn<Player, Integer> colPlayerId;
    @FXML private TableColumn<Player, String> colPlayerName;
    @FXML private TableColumn<Player, Integer> colPlayerNo;
    @FXML private TableColumn<Player, String> colPlayerPos;
    @FXML private TextField tfPlayerName, tfPlayerNo, tfPlayerPos;

    // Controller menggunakan DAO untuk mengakses database dan state untuk selection
    private TeamDao teamDao;
    private PlayerDao playerDao;
    private Team selectedTeam;
    private Player selectedPlayer;

    // Inisialisasi komponen DAO, setup tabel, load data, dan disable form player
    @FXML
    public void initialize() {
        teamDao = new TeamDao();
        playerDao = new PlayerDao();

        setupTables();
        loadTeams();
        
        setPlayerFormState(false);
    }

    // Setup cell value factory untuk semua kolom tabel dan event listener untuk selection
    private void setupTables() {
        // Setup kolom tabel Team menggunakan PropertyValueFactory untuk binding ke property Model
        colTeamId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTeamName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Setup kolom tabel Player menggunakan PropertyValueFactory untuk binding ke property Model
        colPlayerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPlayerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPlayerNo.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber"));
        colPlayerPos.setCellValueFactory(new PropertyValueFactory<>("position"));

        // Event listener untuk selection change di tabel Team menggunakan lambda
        // Saat user memilih team, load pemain team tersebut dan enable form player
        tableTeams.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTeam = newVal;
                tfTeamName.setText(newVal.getName());
                
                loadPlayers(newVal.getId());
                lblSelectedTeam.setText("Manage Pemain: " + newVal.getName());
                setPlayerFormState(true); 
            }
        });

        // Event listener untuk selection change di tabel Player menggunakan lambda
        // Saat user memilih player, isi form dengan data player tersebut
        tablePlayers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedPlayer = newVal;
                tfPlayerName.setText(newVal.getName());
                tfPlayerNo.setText(String.valueOf(newVal.getJerseyNumber()));
                tfPlayerPos.setText(newVal.getPosition());
            }
        });
    }

    // Memuat semua team dari database ke tabel menggunakan ObservableList
    private void loadTeams() {
        tableTeams.setItems(FXCollections.observableArrayList(teamDao.getAll()));
    }

    // Memuat pemain dari team tertentu ke tabel menggunakan ObservableList
    private void loadPlayers(int teamId) {
        tablePlayers.setItems(FXCollections.observableArrayList(playerDao.getPlayersByTeam(teamId)));
    }

    // Mengatur state form player (enable/disable) berdasarkan apakah team sudah dipilih
    private void setPlayerFormState(boolean active) {
        tablePlayers.setDisable(!active);
        tfPlayerName.setDisable(!active);
        tfPlayerNo.setDisable(!active);
        tfPlayerPos.setDisable(!active);
    }

    // CRUD Operations untuk Team
    
    // Menambahkan team baru ke database
    @FXML
    private void addTeam() {
        String name = tfTeamName.getText();
        if (name.isEmpty()) return;

        Team t = new Team(name, null); 
        
        if (teamDao.add(t)) {
            loadTeams();
            clearTeamForm();
            AlertHelper.showWarning("Info", "Tim berhasil ditambahkan!");
        }
    }

    // Mengupdate team yang dipilih di database
    @FXML
    private void updateTeam() {
        if (selectedTeam == null) return;
        selectedTeam.setName(tfTeamName.getText());
        
        if (teamDao.update(selectedTeam)) {
            loadTeams();
            clearTeamForm();
        }
    }

    // Menghapus team yang dipilih dari database
    @FXML
    private void deleteTeam() {
        if (selectedTeam == null) return;
        
        if (teamDao.delete(selectedTeam.getId())) {
            loadTeams();
            clearTeamForm();
            tablePlayers.getItems().clear();
            setPlayerFormState(false);
        }
    }

    // Reset form team dan clear selection
    @FXML
    private void clearTeamForm() {
        tfTeamName.clear();
        selectedTeam = null;
        tableTeams.getSelectionModel().clearSelection();
    }

    // CRUD Operations untuk Player

    // Menambahkan player baru ke team yang dipilih dengan validasi input
    @FXML
    private void addPlayer() {
        if (selectedTeam == null) return;
        
        try {
            String name = tfPlayerName.getText();
            int number = Integer.parseInt(tfPlayerNo.getText());
            String pos = tfPlayerPos.getText();

            Player p = new Player(selectedTeam.getId(), name, number, pos);
            if (playerDao.addPlayer(p)) {
                loadPlayers(selectedTeam.getId());
                clearPlayerForm();
            }
        } catch (NumberFormatException e) {
        	AlertHelper.showWarning("Error", "Nomor Punggung harus angka!");
        }
    }

    // Mengupdate player yang dipilih di database dengan validasi input
    @FXML
    private void updatePlayer() {
        if (selectedPlayer == null) return;

        try {
            selectedPlayer.setName(tfPlayerName.getText());
            selectedPlayer.setJerseyNumber(Integer.parseInt(tfPlayerNo.getText()));
            selectedPlayer.setPosition(tfPlayerPos.getText());

            if (playerDao.updatePlayer(selectedPlayer)) {
                loadPlayers(selectedTeam.getId());
                clearPlayerForm();
            }
        } catch (NumberFormatException e) {
        	AlertHelper.showWarning("Error", "Nomor Punggung harus angka!");
        }
    }

    // Menghapus player yang dipilih dari database
    @FXML
    private void deletePlayer() {
        if (selectedPlayer == null) return;

        if (playerDao.deletePlayer(selectedPlayer.getId())) {
            loadPlayers(selectedTeam.getId());
            clearPlayerForm();
        }
    }

    // Reset form player dan clear selection
    @FXML
    private void clearPlayerForm() {
        tfPlayerName.clear();
        tfPlayerNo.clear();
        tfPlayerPos.clear();
        selectedPlayer = null;
        tablePlayers.getSelectionModel().clearSelection();
    }
}