package model.rules;

import java.util.Arrays;
import java.util.List;

// BadmintonStrategy mengimplementasikan SportStrategy untuk aturan Badminton (Polymorphism)
public class BadmintonStrategy implements SportStrategy {

    // Menghitung skor baru untuk badminton (selalu +1)
    @Override
    public int calculateNewScore(int currentScore, int pointsToAdd) {
        return currentScore + 1; 
    }

    // Mengembalikan opsi poin yang valid untuk badminton
    @Override
    public List<Integer> getValidPointOptions() {
        return Arrays.asList(1);
    }

    // Mengecek apakah pemain badminton terkena foul out
    @Override
    public boolean isFoulOut(int totalFouls) {
        return totalFouls >= 2;
    }

    // Mendapatkan pesan foul untuk badminton
    @Override
    public String getFoulMessage() {
        return "Disked (Kartu Merah/Hitam)";
    }
}