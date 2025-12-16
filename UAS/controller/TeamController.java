package controller;

import dao.PlayerDao;
import dao.TeamDao;
import model.Player;
import model.Team;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class TeamController {

    // --- UI Components Team ---
    @FXML private TableView<Team> tableTeams;
    @FXML private TableColumn<Team, Integer> colTeamId;   // <--- TAMBAHAN BARU
    @FXML private TableColumn<Team, String> colTeamName;
    @FXML private TextField tfTeamName;

    // --- UI Components Player ---
    @FXML private Label lblSelectedTeam;
    @FXML private TableView<Player> tablePlayers;
    @FXML private TableColumn<Player, Integer> colPlayerId; // <--- TAMBAHAN BARU
    @FXML private TableColumn<Player, String> colPlayerName;
    @FXML private TableColumn<Player, Integer> colPlayerNo;
    @FXML private TableColumn<Player, String> colPlayerPos;
    @FXML private TextField tfPlayerName, tfPlayerNo, tfPlayerPos;

    // --- DAO & Data ---
    private TeamDao teamDao;
    private PlayerDao playerDao;
    private Team selectedTeam;
    private Player selectedPlayer;

    @FXML
    public void initialize() {
        teamDao = new TeamDao();
        playerDao = new PlayerDao();

        setupTables();
        loadTeams();
        
        setPlayerFormState(false);
    }

    private void setupTables() {
        // --- Setup Kolom Tabel Team ---
        colTeamId.setCellValueFactory(new PropertyValueFactory<>("id"));     // <--- Mapping ID
        colTeamName.setCellValueFactory(new PropertyValueFactory<>("name")); // Mapping Nama

        // --- Setup Kolom Tabel Player ---
        colPlayerId.setCellValueFactory(new PropertyValueFactory<>("id"));           // <--- Mapping ID
        colPlayerName.setCellValueFactory(new PropertyValueFactory<>("name"));       // Mapping Nama
        colPlayerNo.setCellValueFactory(new PropertyValueFactory<>("jerseyNumber")); // Mapping No Punggung
        colPlayerPos.setCellValueFactory(new PropertyValueFactory<>("position"));    // Mapping Posisi

        // Listener Seleksi Tabel Tim
        tableTeams.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTeam = newVal;
                tfTeamName.setText(newVal.getName());
                
                loadPlayers(newVal.getId());
                lblSelectedTeam.setText("Manage Pemain: " + newVal.getName());
                setPlayerFormState(true); 
            }
        });

        // Listener Seleksi Tabel Pemain
        tablePlayers.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedPlayer = newVal;
                tfPlayerName.setText(newVal.getName());
                tfPlayerNo.setText(String.valueOf(newVal.getJerseyNumber()));
                tfPlayerPos.setText(newVal.getPosition());
            }
        });
    }

    private void loadTeams() {
        tableTeams.setItems(FXCollections.observableArrayList(teamDao.getAll()));
    }

    private void loadPlayers(int teamId) {
        tablePlayers.setItems(FXCollections.observableArrayList(playerDao.getPlayersByTeam(teamId)));
    }

    private void setPlayerFormState(boolean active) {
        tablePlayers.setDisable(!active);
        tfPlayerName.setDisable(!active);
        tfPlayerNo.setDisable(!active);
        tfPlayerPos.setDisable(!active);
    }

    // === CRUD TIM ===
    
    @FXML
    private void addTeam() {
        String name = tfTeamName.getText();
        if (name.isEmpty()) return;

        // Constructor: (Nama, LogoPath=null)
        Team t = new Team(name, null); 
        
        if (teamDao.add(t)) {
            loadTeams();
            clearTeamForm();
            showAlert("Info", "Tim berhasil ditambahkan!");
        }
    }

    @FXML
    private void updateTeam() {
        if (selectedTeam == null) return;
        selectedTeam.setName(tfTeamName.getText());
        
        if (teamDao.update(selectedTeam)) {
            loadTeams();
            clearTeamForm();
        }
    }

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

    @FXML
    private void clearTeamForm() {
        tfTeamName.clear();
        selectedTeam = null;
        tableTeams.getSelectionModel().clearSelection();
    }

    // === CRUD PEMAIN ===

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
            showAlert("Error", "Nomor Punggung harus angka!");
        }
    }

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
            showAlert("Error", "Nomor Punggung harus angka!");
        }
    }

    @FXML
    private void deletePlayer() {
        if (selectedPlayer == null) return;

        if (playerDao.deletePlayer(selectedPlayer.getId())) {
            loadPlayers(selectedTeam.getId());
            clearPlayerForm();
        }
    }

    @FXML
    private void clearPlayerForm() {
        tfPlayerName.clear();
        tfPlayerNo.clear();
        tfPlayerPos.clear();
        selectedPlayer = null;
        tablePlayers.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}