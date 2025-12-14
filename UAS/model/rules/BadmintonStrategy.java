package model.rules;

import java.util.Arrays;
import java.util.List;

public class BadmintonStrategy implements SportStrategy {

    @Override
    public int calculateNewScore(int currentScore, int pointsToAdd) {
        // Badminton selalu +1 (Rally Point)
        // Walaupun operator kirim +3, kita paksa jadi +1 atau return error
        return currentScore + 1; 
    }

    @Override
    public List<Integer> getValidPointOptions() {
        return Arrays.asList(1); // Di UI nanti tombol cuma muncul +1
    }

    @Override
    public boolean isFoulOut(int totalFouls) {
        return totalFouls >= 2; // Misal: 2 Kartu Kuning = Merah (Simulasi)
    }

    @Override
    public String getFoulMessage() {
        return "Disked (Kartu Merah/Hitam)";
    }
}