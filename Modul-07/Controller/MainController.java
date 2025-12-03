package Modul_07.Controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import Modul_07.Model.Buku;
import Modul_07.Model.Pelanggan;
import Modul_07.Model.Penjualan;
import Modul_07.Dao.BukuDAO;
import Modul_07.Dao.PelangganDAO;
import Modul_07.Dao.PenjualanDAO;
import Modul_07.Util.ValidationHelper;
import Modul_07.Util.FormatterHelper;

public class MainController {
    // --- SETUP PELANGGAN ---
    @FXML private TextField tfNamaPelanggan, tfEmailPelanggan, tfTeleponPelanggan, tfNamaPelangganJual;
    @FXML private Button btnAddPelanggan, btnEditPelanggan, btnDeletePelanggan;
    @FXML private TableView<Pelanggan> tablePelanggan;
    @FXML private TableColumn<Pelanggan, String> colNamaPelanggan, colEmailPelanggan, colTeleponPelanggan;

    // --- SETUP BUKU ---
    @FXML private TextField tfJudulBuku, tfPenulisBuku, tfHargaBuku, tfStokBuku, tfJudulBukuJual;
    @FXML private Button btnAddBuku, btnEditBuku, btnDeleteBuku;
    @FXML private TableView<Buku> tableBuku;
    @FXML private TableColumn<Buku, String> colJudulBuku, colPenulisBuku;
    @FXML private TableColumn<Buku, Double> colHargaBuku;
    @FXML private TableColumn<Buku, Integer> colStokBuku;
    
    // --- SETUP PENJUALAN---
    @FXML private TextField tfIdPelangganJual, tfIdBukuJual, tfJumlahJual, tfTanggalJual;
    @FXML private Button btnAddPenjualan, btnDeletePenjualan;
    @FXML private TableView<Penjualan> tablePenjualan;
    @FXML private TableColumn<Penjualan, Integer> colPelangganJual, colBukuJual, colJumlahJual;
    @FXML private TableColumn<Penjualan, Double> colTotalJual;
    @FXML private TableColumn<Penjualan, String> colTanggalJual;

    @FXML
    public void initialize() {
        // --- SETUP PELANGGAN ---
        colNamaPelanggan.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colEmailPelanggan.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTeleponPelanggan.setCellValueFactory(new PropertyValueFactory<>("telepon"));

        loadDataPelanggan();
        
        btnAddPelanggan.setOnAction(e -> tambahPelanggan());
        btnDeletePelanggan.setOnAction(e -> hapusPelanggan());
        btnEditPelanggan.setOnAction(e -> editPelanggan());
        
        tablePelanggan.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tfNamaPelanggan.setText(newSelection.getNama());
                tfEmailPelanggan.setText(newSelection.getEmail());
                tfTeleponPelanggan.setText(newSelection.getTelepon());
            }
        });
        
        // --- SETUP BUKU ---
        colJudulBuku.setCellValueFactory(new PropertyValueFactory<>("judul"));
        colPenulisBuku.setCellValueFactory(new PropertyValueFactory<>("penulis"));
        colHargaBuku.setCellValueFactory(new PropertyValueFactory<>("harga"));
        FormatterHelper.setCurrencyFormat(colHargaBuku);
        
        colStokBuku.setCellValueFactory(new PropertyValueFactory<>("stok"));

        loadDataBuku();

        btnAddBuku.setOnAction(e -> tambahBuku());
        btnEditBuku.setOnAction(e -> editBuku());
        btnDeleteBuku.setOnAction(e -> hapusBuku());

        tableBuku.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tfJudulBuku.setText(newSelection.getJudul());
                tfPenulisBuku.setText(newSelection.getPenulis());
                tfHargaBuku.setText(String.valueOf(newSelection.getHarga()));
                tfStokBuku.setText(String.valueOf(newSelection.getStok()));
            }
        });
        
        // --- SETUP PENJUALAN ---
        colPelangganJual.setCellValueFactory(new PropertyValueFactory<>("pelanggan_id"));
        colBukuJual.setCellValueFactory(new PropertyValueFactory<>("buku_id"));
        colJumlahJual.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colTotalJual.setCellValueFactory(new PropertyValueFactory<>("total_harga"));
        FormatterHelper.setCurrencyFormat(colTotalJual);
        colTanggalJual.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        loadDataPenjualan();

        btnAddPenjualan.setOnAction(e -> tambahPenjualan());
        btnDeletePenjualan.setOnAction(e -> hapusPenjualan());
    }
    
    // --- LOGIKA PELANGGAN ---
    private void loadDataPelanggan() {
        ObservableList<Pelanggan> list = PelangganDAO.getAllPelanggan();
        tablePelanggan.setItems(list);
    }
    
    private void tambahPelanggan() {
        String nama = tfNamaPelanggan.getText().trim();
        String email = tfEmailPelanggan.getText().trim();
        String telepon = tfTeleponPelanggan.getText().trim();

        if (ValidationHelper.isEmpty(nama, email, telepon)) {
            showAlert("Error Input", "Semua field harus diisi!");
            return;
        }

        if (!ValidationHelper.isValidEmail(email)) {
            showAlert("Error Input", "Format email tidak valid! Contoh: nama@email.com");
            return;
        }

        if (!ValidationHelper.isValidTelepon(telepon)) {
            showAlert("Error Input", "Nomor telepon harus berupa angka!");
            return;
        }

        Pelanggan p = new Pelanggan(0, nama, email, telepon);
        PelangganDAO.addPelanggan(p);
        loadDataPelanggan();
        tfNamaPelanggan.clear(); tfEmailPelanggan.clear(); tfTeleponPelanggan.clear();
    }
    
    private void hapusPelanggan() {
        Pelanggan selectedPelanggan = tablePelanggan.getSelectionModel().getSelectedItem();
        if (selectedPelanggan == null) {
            showAlert("Peringatan", "Pilih data dulu di tabel!");
            return;
        }
        PelangganDAO.deletePelanggan(selectedPelanggan.getPelanggan_id());
        loadDataPelanggan();
    }
    
    private void editPelanggan() {
        Pelanggan selected = tablePelanggan.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Peringatan", "Pilih data yang mau diedit dulu!");
            return;
        }
        
        String nama = tfNamaPelanggan.getText().trim();
        String email = tfEmailPelanggan.getText().trim();
        String telepon = tfTeleponPelanggan.getText().trim();
        
        if (ValidationHelper.isEmpty(nama, email, telepon)) {
            showAlert("Error Input", "Semua field harus diisi!");
            return;
        }
        if (!ValidationHelper.isValidEmail(email)) {
            showAlert("Error Input", "Format email tidak valid!");
            return;
        }
        if (!ValidationHelper.isValidTelepon(telepon)) {
            showAlert("Error Input", "Nomor telepon harus berupa angka!");
            return;
        }
        
        Pelanggan p = new Pelanggan(selected.getPelanggan_id(), nama, email, telepon);
        PelangganDAO.updatePelanggan(p);
        loadDataPelanggan();
        tfNamaPelanggan.clear(); tfEmailPelanggan.clear(); tfTeleponPelanggan.clear();
    }
    
    // --- LOGIKA BUKU ---
    private void loadDataBuku() {
        ObservableList<Buku> list = BukuDAO.getAllBuku();
        tableBuku.setItems(list);
    }

    private void tambahBuku() {
        String judul = tfJudulBuku.getText().trim();
        String penulis = tfPenulisBuku.getText().trim();
        String hargaStr = tfHargaBuku.getText().trim();
        String stokStr = tfStokBuku.getText().trim();

        if (ValidationHelper.isEmpty(judul, penulis, hargaStr, stokStr)) {
            showAlert("Error Input", "Semua field harus diisi!");
            return;
        }
        
        if (!ValidationHelper.isNumeric(hargaStr) || !ValidationHelper.isNumeric(stokStr)) {
            showAlert("Error Input", "Harga dan Stok harus berupa angka!");
            return;
        }

        double harga = Double.parseDouble(hargaStr);
        int stok = Integer.parseInt(stokStr);

        if (ValidationHelper.isNegative(harga) || ValidationHelper.isNegative(stok)) {
            showAlert("Error Input", "Harga/Stok tidak boleh negatif!");
            return;
        }

        Buku b = new Buku(0, judul, penulis, harga, stok);
        BukuDAO.addBuku(b);
        loadDataBuku();
        clearFormBuku();
    }

    private void editBuku() {
        Buku selected = tableBuku.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Peringatan", "Pilih buku dulu!");
            return;
        }
        
        String judul = tfJudulBuku.getText().trim();
        String penulis = tfPenulisBuku.getText().trim();
        String hargaStr = tfHargaBuku.getText().trim();
        String stokStr = tfStokBuku.getText().trim();

        if (ValidationHelper.isEmpty(judul, penulis, hargaStr, stokStr)) {
            showAlert("Error Input", "Semua field harus diisi!");
            return;
        }

        if (!ValidationHelper.isNumeric(hargaStr) || !ValidationHelper.isNumeric(stokStr)) {
            showAlert("Error Input", "Harga dan Stok harus berupa angka!");
            return;
        }
        
        double harga = Double.parseDouble(hargaStr);
        int stok = Integer.parseInt(stokStr);

        if (ValidationHelper.isNegative(harga) || ValidationHelper.isNegative(stok)) {
            showAlert("Error Input", "Harga/Stok tidak boleh negatif!");
            return;
        }

        Buku b = new Buku(selected.getBuku_id(), judul, penulis, harga, stok);
        BukuDAO.updateBuku(b);
        loadDataBuku();
        clearFormBuku();
    }

    private void hapusBuku() {
        Buku selected = tableBuku.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Peringatan", "Pilih buku dulu!");
            return;
        }
        BukuDAO.deleteBuku(selected.getBuku_id());
        loadDataBuku();
        clearFormBuku();
    }

    private void clearFormBuku() {
        tfJudulBuku.clear(); tfPenulisBuku.clear(); tfHargaBuku.clear(); tfStokBuku.clear();
    }
    
    // --- LOGIKA PENJUALAN ---
    private void loadDataPenjualan() {
        tablePenjualan.setItems(PenjualanDAO.getAllPenjualan());
    }

    private void tambahPenjualan() {
        String namaPel = tfNamaPelangganJual.getText().trim();
        String judulBuku = tfJudulBukuJual.getText().trim();
        String jumlahStr = tfJumlahJual.getText().trim();
        String tanggal = tfTanggalJual.getText().trim();

        // 1. Validasi Kosong
        if (ValidationHelper.isEmpty(namaPel, judulBuku, jumlahStr, tanggal)) {
            showAlert("Error Input", "Semua field harus diisi!");
            return;
        }

        // 2. Validasi Tanggal
        if (!ValidationHelper.isValidTanggal(tanggal)) {
            showAlert("Error Input", "Format tanggal salah! Gunakan format: YYYY-MM-DD");
            return;
        }
        
        // 3. Validasi Jumlah (Harus Angka)
        if (!ValidationHelper.isNumeric(jumlahStr)) {
             showAlert("Error Input", "Jumlah harus berupa angka!");
             return;
        }

        try {
            int jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) {
                showAlert("Error Input", "Jumlah harus lebih dari 0!");
                return;
            }

            // --- LOGIKA PENCARIAN ID PELANGGAN BERDASARKAN NAMA ---
            int pelIdKetemu = -1; // -1 artinya belum ketemu
            
            // Loop semua data di tabel Pelanggan
            for (Pelanggan p : tablePelanggan.getItems()) {
                // Cek nama (IgnoreCase agar tidak sensitif huruf besar/kecil)
                if (p.getNama().equalsIgnoreCase(namaPel)) {
                    pelIdKetemu = p.getPelanggan_id();
                    break; 
                }
            }

            if (pelIdKetemu == -1) {
                showAlert("Error", "Pelanggan dengan nama '" + namaPel + "' tidak ditemukan!");
                return;
            }

            // --- LOGIKA PENCARIAN ID BUKU & HARGA BERDASARKAN JUDUL ---
            int bukuIdKetemu = -1;
            double hargaBuku = 0;
            
            for (Buku b : tableBuku.getItems()) {
                if (b.getJudul().equalsIgnoreCase(judulBuku)) {
                    // Cek Stok
                    if (b.getStok() < jumlah) {
                        showAlert("Error", "Stok buku '" + b.getJudul() + "' tidak cukup! Tersisa: " + b.getStok());
                        return;
                    }
                    bukuIdKetemu = b.getBuku_id();
                    hargaBuku = b.getHarga();
                    break;
                }
            }

            if (bukuIdKetemu == -1) {
                showAlert("Error", "Buku dengan judul '" + judulBuku + "' tidak ditemukan!");
                return;
            }

            // --- SIMPAN TRANSAKSI ---
            double totalHarga = hargaBuku * jumlah;

            Penjualan p = new Penjualan(0, jumlah, totalHarga, tanggal, pelIdKetemu, bukuIdKetemu);
            PenjualanDAO.addPenjualan(p);
            
            loadDataPenjualan();
            
            // Clear Form
            tfNamaPelangganJual.clear(); 
            tfJudulBukuJual.clear(); 
            tfJumlahJual.clear(); 
            tfTanggalJual.clear();

        } catch (NumberFormatException e) {
            showAlert("Error Input", "Terjadi kesalahan input angka.");
        }
    }

    private void hapusPenjualan() {
        Penjualan sel = tablePenjualan.getSelectionModel().getSelectedItem();
        if (sel != null) {
            PenjualanDAO.deletePenjualan(sel.getPenjualan_id());
            loadDataPenjualan();
        } else {
            showAlert("Peringatan", "Pilih transaksi dulu!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}