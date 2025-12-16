package model.rules;

import java.util.Arrays;
import java.util.List;

// BasketballStrategy mengimplementasikan SportStrategy untuk aturan Basket (Polymorphism)
public class BasketballStrategy implements SportStrategy {

    // Menghitung skor baru untuk basket
    @Override
    public int calculateNewScore(int currentScore, int pointsToAdd) {
        return currentScore + pointsToAdd;
    }

    // Mengembalikan opsi poin yang valid untuk basket
    @Override
    public List<Integer> getValidPointOptions() {
        return Arrays.asList(1, 2, 3);
    }

    // Mengecek apakah pemain basket terkena foul out
    @Override
    public boolean isFoulOut(int totalFouls) {
        return totalFouls >= 5;
    }

    // Mendapatkan pesan foul untuk basket
    @Override
    public String getFoulMessage() {
        return "Foul Out (Maksimum 5 Pelanggaran)";
    }
}