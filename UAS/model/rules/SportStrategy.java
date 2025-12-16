package model.rules;

import java.util.List;

// Interface SportStrategy mendefinisikan kontrak untuk aturan olahraga yang berbeda (Polymorphism dan Abstraction)
public interface SportStrategy {
    // Menghitung skor baru berdasarkan poin yang ditambahkan sesuai aturan olahraga
    int calculateNewScore(int currentScore, int pointsToAdd);

    // Mengembalikan daftar opsi poin yang valid untuk ditampilkan di UI
    List<Integer> getValidPointOptions();

    // Mengecek apakah pemain sudah mencapai batas maksimal foul (Foul Out)
    boolean isFoulOut(int totalFouls);
    
    // Mendapatkan pesan foul yang spesifik untuk olahraga
    String getFoulMessage();
}