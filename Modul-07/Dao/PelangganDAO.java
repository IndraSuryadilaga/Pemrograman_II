package Modul_07.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Modul_07.Model.Pelanggan;
import Modul_07.Util.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PelangganDAO {
	public static void addPelanggan(Pelanggan pelanggan) {
        String sql = "INSERT INTO Pelanggan (nama, email, telepon) VALUES (?, ?, ?)";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, pelanggan.getNama());
            ps.setString(2, pelanggan.getEmail());
            ps.setString(3, pelanggan.getTelepon());
            
            ps.executeUpdate();
            
            conn.close();
            System.out.println("Data berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Error menambah data: " + e.getMessage());
        }
    }
	
    public static void deletePelanggan(int id) {
        String sql = "DELETE FROM Pelanggan WHERE pelanggan_id = ?";
        
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
	
    public static void updatePelanggan(Pelanggan pelanggan) {
        String sql = "UPDATE Pelanggan SET nama = ?, email = ?, telepon = ? WHERE pelanggan_id = ?";
        
        try {
            Connection conn = DatabaseHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, pelanggan.getNama());
            ps.setString(2, pelanggan.getEmail());
            ps.setString(3, pelanggan.getTelepon());
            ps.setInt(4, pelanggan.getPelanggan_id());
            
            ps.executeUpdate();
            
            conn.close();
            System.out.println("Data berhasil diubah!");
        } catch (SQLException e) {
            System.out.println("Error mengubah data: " + e.getMessage());
        }
    }
	
	public static ObservableList<Pelanggan> getAllPelanggan() {
		ObservableList<Pelanggan> listPelanggan = FXCollections.observableArrayList();
		String Query = "SELECT * FROM Pelanggan";
		
		try {
			Connection conn = DatabaseHelper.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(Query);
			
			while (rs.next()) {
				int id = rs.getInt("pelanggan_id");
				String nama = rs.getString("nama");
				String email = rs.getString("email");
				String telepon = rs.getString("telepon");
				Pelanggan p =new Pelanggan(id, nama, email, telepon);
				
				listPelanggan.add(p);
			}
			
			conn.close();
		}
		catch (SQLException e) {
            System.out.println("Error mengambil data pelanggan: " + e.getMessage());
        }
		
		return listPelanggan;	
	}
}
