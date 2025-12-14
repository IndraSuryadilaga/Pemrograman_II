package model.rules;

import java.util.Arrays;
import java.util.List;

public class BasketballStrategy implements SportStrategy {

    @Override
    public int calculateNewScore(int currentScore, int pointsToAdd) {
        return currentScore + pointsToAdd;
    }

    @Override
    public List<Integer> getValidPointOptions() {
        return Arrays.asList(1, 2, 3); // Di UI nanti tombol muncul +1, +2, +3
    }

    @Override
    public boolean isFoulOut(int totalFouls) {
        return totalFouls >= 5; // Aturan NBA: 6, FIBA: 5. Kita pakai 5.
    }

    @Override
    public String getFoulMessage() {
        return "Foul Out (Maksimum 5 Pelanggaran)";
    }
}