package helper;

import java.util.Map;
import java.util.stream.Collectors;

public class FormatterHelper {

    private FormatterHelper() {}

    // Method ini sebelumnya ada di DashboardController & TournamentController
    public static String formatScoreDetail(Map<Integer, Integer> scores) {
        if (scores == null || scores.isEmpty()) return "";
        
        // Mengubah Map skor menjadi string format "(10, 15, 12, ...)"
        return scores.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> String.valueOf(e.getValue()))
                .collect(Collectors.joining(", ", "(", ")"));
    }
}