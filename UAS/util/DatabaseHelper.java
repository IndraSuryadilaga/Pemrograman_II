package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/sporta_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Variable Singleton Instance
    private static Connection connection;

    // Private Constructor (Supaya tidak bisa di-new sembarangan)
    private DatabaseHelper() { }

    // Method untuk mendapatkan koneksi (Static Factory Method)
    public static Connection getConnection() {
        try {
            // Cek jika koneksi belum ada atau sudah tertutup
            if (connection == null || connection.isClosed()) {
                // Load Driver MySQL (Opsional di Java baru, tapi bagus untuk kompatibilitas)
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Buat koneksi baru
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Sukses terhubung ke Database: sporta_db");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Gagal koneksi database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    // Method main untuk testing langsung (Run File ini saja untuk cek koneksi)
    public static void main(String[] args) {
        getConnection();
    }
}