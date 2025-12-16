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

// Controller utama yang mengatur navigasi antar view menggunakan Singleton Pattern
public class MainController {

    // Singleton Pattern: instance static untuk akses global dari controller lain
    private static MainController instance;

    // Method static untuk mendapatkan instance singleton
    public static MainController getInstance() {
        return instance;
    }

    // Komponen UI yang di-bind dari MainLayout.fxml
    @FXML private StackPane contentArea;
    
    // Tombol-tombol menu navigasi
    @FXML private Button btnDashboard;
    @FXML private Button btnNewTournament;
    @FXML private Button btnTeam;
    @FXML private Button btnHistory;

    // Inisialisasi: set instance singleton dan tampilkan dashboard sebagai default view
    @FXML
    public void initialize() {
        instance = this;
        showDashboard();
    }
    
    // Menampilkan halaman Dashboard
    @FXML 
    public void showDashboard() {
        loadView("/view/DashboardView.fxml");
        setActiveButton(btnDashboard); 
    }

    // Menampilkan halaman New Tournament
    @FXML 
    public void showNewTournament() {
        loadView("/view/NewTournamentView.fxml");
        setActiveButton(btnNewTournament); 
    }

    // Menampilkan halaman Team Data
    @FXML 
    public void showTeamData() {
        loadView("/view/TeamView.fxml");
        setActiveButton(btnTeam); 
    }

    // Menampilkan halaman History
    @FXML 
    public void showHistory() {
        loadView("/view/HistoryView.fxml");
        setActiveButton(btnHistory); 
    }

    // Membuka halaman Match Operator untuk mengoperasikan match tertentu (dipanggil dari controller lain)
    public void openMatchOperator(Match match) {
        try {
            // Load FXML view dan controller menggunakan FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MatchOperatorView.fxml"));
            Parent view = loader.load();

            // Ambil controller dari loader untuk mengirim data
            MatchOperatorController controller = loader.getController();
            
            // Tentukan jenis olahraga (Basket/Badminton) berdasarkan tournament ID dari match
            String sportName = "Basket";
            TournamentDao tournamentDao = new TournamentDao();
            
            // Ambil data turnamen dari database untuk mendapatkan sportId
            Tournament t = tournamentDao.get(match.getTournamentId());
            
            // Mapping sportId ke nama olahraga
            if (t != null) {
                if (t.getSportId() == 1) sportName = "Basket";
                if (t.getSportId() == 2) sportName = "Badminton";
            }

            // Kirim data match dan nama olahraga ke controller
            controller.setMatchData(match, sportName);

            // Ganti tampilan utama dengan view Match Operator
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
            // Matikan highlight tombol menu karena sedang tidak di menu utama
            setActiveButton(null);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal membuka Match Operator: " + e.getMessage());
        }
    }

    // Mengatur tombol menu yang aktif dengan menambahkan/menghapus CSS class "active"
    private void setActiveButton(Button activeButton) {
        // Reset semua tombol: hapus class "active" dari semua tombol menu
        if(btnDashboard != null) btnDashboard.getStyleClass().remove("active");
        if(btnNewTournament != null) btnNewTournament.getStyleClass().remove("active");
        if(btnTeam != null) btnTeam.getStyleClass().remove("active");
        if(btnHistory != null) btnHistory.getStyleClass().remove("active");

        // Set tombol aktif: tambahkan class "active" ke tombol yang dipilih
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
        }
    }

    // Load view dari FXML dan mengganti konten di contentArea
    public void loadView(String fxmlPath) {
        try {
            // Load FXML view menggunakan FXMLLoader
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            
            // Ganti konten area dengan view baru
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Gagal load view: " + fxmlPath);
        }
    }
}