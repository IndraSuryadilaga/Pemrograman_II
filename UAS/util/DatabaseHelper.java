package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Helper class untuk mengelola koneksi database secara terpusat menggunakan pola Singleton
public class DatabaseHelper {
    
    // Konfigurasi URL, username, dan password untuk koneksi ke database MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/sporta_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Menyimpan satu instance koneksi yang akan digunakan berulang kali
    private static Connection connection;

    // Constructor private untuk mencegah pembuatan object baru dari luar class
    private DatabaseHelper() { }

    // Mengambil koneksi database yang aktif atau membuat baru jika belum ada
    public static Connection getConnection() {
        try {
            // Cek apakah koneksi belum dibuat atau sudah terputus, jika ya buat baru
            if (connection == null || connection.isClosed()) {
                // Memuat driver JDBC MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Membuka koneksi menggunakan kredensial yang sudah disiapkan
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Sukses terhubung ke Database");
            }
        } catch (ClassNotFoundException | SQLException e) {
            // Menampilkan pesan error jika driver tidak ditemukan atau login gagal
            System.err.println("Gagal koneksi database: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    // Method main untuk menguji apakah konfigurasi koneksi sudah benar tanpa menjalankan aplikasi utama
    public static void main(String[] args) {
        getConnection();
    }
}