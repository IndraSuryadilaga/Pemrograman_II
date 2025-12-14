package model.rules;

import java.util.List;

public interface SportStrategy {
    // Menghitung skor baru (misal: Basket +3, Badminton +1)
    int calculateNewScore(int currentScore, int pointsToAdd);

    // Mengembalikan opsi poin yang valid untuk UI (misal: Basket [1,2,3], Badminton [1])
    List<Integer> getValidPointOptions();

    // Cek apakah jumlah foul sudah batas maksimal (Foul Out)
    boolean isFoulOut(int totalFouls);
    
    // Mendapatkan pesan foul (misal: "Foul Out" vs "Red Card")
    String getFoulMessage();
}