package Modul_07.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Modul_07.Model.Penjualan;
import Modul_07.Util.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PenjualanDAO {
	public static void addPenjualan(Penjualan p) {
        String sql = "INSERT INTO Penjualan (jumlah, total_harga, tanggal, pelanggan_id, buku_id) VALUES (?, ?, ?, ?, ?)";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setInt(1, p.getJumlah());
            ps.setDouble(2, p.getTotal_harga());
            ps.setString(3, p.getTanggal());
            ps.setInt(4, p.getPelanggan_id());
            ps.setInt(5, p.getBuku_id());
            
            ps.executeUpdate();
            
            conn.close();
            System.out.println("Transaksi berhasil disimpan!");
        } catch (SQLException e) {
            System.out.println("Error tambah transaksi: " + e.getMessage());
        }
    }

    // === READ ===
    public static ObservableList<Penjualan> getAllPenjualan() {
        ObservableList<Penjualan> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Penjualan";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Penjualan p = new Penjualan(
                    rs.getInt("penjualan_id"),
                    rs.getInt("jumlah"),
                    rs.getDouble("total_harga"),
                    rs.getString("tanggal"), // Ambil tanggal sebagai String
                    rs.getInt("pelanggan_id"),
                    rs.getInt("buku_id")
                );
                list.add(p);
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error ambil data transaksi: " + e.getMessage());
        }
        return list;
    }

    // === DELETE ===
    public static void deletePenjualan(int id) {
        String sql = "DELETE FROM Penjualan WHERE penjualan_id = ?";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            conn.close();
            System.out.println("Transaksi berhasil dihapus!");
        } catch (SQLException e) {
            System.out.println("Error hapus transaksi: " + e.getMessage());
        }
    }
}
