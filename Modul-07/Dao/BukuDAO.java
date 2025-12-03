package Modul_07.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Modul_07.Model.Buku;
import Modul_07.Model.Pelanggan;
import Modul_07.Util.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BukuDAO {
	public static void addBuku(Buku buku) {
        String sql = "INSERT INTO Buku (judul, penulis, harga, stok) VALUES (?, ?, ?, ?)";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, buku.getJudul());
            ps.setString(2, buku.getPenulis());
            ps.setDouble(3, buku.getHarga());
            ps.setInt(4, buku.getStok());
            
            ps.executeUpdate();
            
            conn.close();
            System.out.println("Data berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Error menambah data: " + e.getMessage());
        }
    }
	
    public static void deleteBuku(int id) {
        String sql = "DELETE FROM Buku WHERE buku_id = ?";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setInt(1, id);
            
            ps.executeUpdate();
            
            conn.close();
            System.out.println("Data berhasil dihapus!");
        } catch (SQLException e) {
            System.out.println("Error menghapus data: " + e.getMessage());
        }
    }
    
    public static void updateBuku(Buku buku) {
        String sql = "UPDATE Buku SET judul = ?, penulis = ?, harga = ?, stok = ? WHERE buku_id = ?";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, buku.getJudul());
            ps.setString(2, buku.getPenulis());
            ps.setDouble(3, buku.getHarga());
            ps.setInt(4, buku.getStok());
            ps.setInt(5, buku.getBuku_id());
            
            ps.executeUpdate();
            
            conn.close();
            System.out.println("Data berhasil diubah!");
        } catch (SQLException e) {
            System.out.println("Error mengubah data: " + e.getMessage());
        }
    }
	
	public static ObservableList<Buku> getAllBuku() {
		ObservableList<Buku> listBuku = FXCollections.observableArrayList();
		String Query = "SELECT * FROM Buku";
		
		try {
			Connection conn = DatabaseHelper.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(Query);
			
			while (rs.next()) {
				int id = rs.getInt("buku_id");
				String judul = rs.getString("Judul");
				String penulis = rs.getString("Penulis");
				double harga = rs.getDouble("Harga");
				int stok = rs.getInt("Stok");
				Buku b = new Buku(id, judul, penulis, harga, stok);
				
				listBuku.add(b);
			}
			
			conn.close();
		}
		catch (SQLException e) {
            System.out.println("Error mengambil data buku: " + e.getMessage());
        }
		
		return listBuku;	
	}
}