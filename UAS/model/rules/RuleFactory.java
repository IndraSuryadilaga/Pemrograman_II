package model.rules;

// RuleFactory menyediakan method untuk mendapatkan SportStrategy berdasarkan nama olahraga (Factory Pattern)
public class RuleFactory {
    
    // Mengembalikan implementasi SportStrategy yang sesuai dengan nama olahraga
    public static SportStrategy getStrategy(String sportName) {
        if (sportName == null) return new BasketballStrategy();

        switch (sportName.toLowerCase()) {
            case "basketball":
            case "basket":
                return new BasketballStrategy();
                
            case "badminton":
            case "bulutangkis":
                return new BadmintonStrategy();
                
            default:
                return new BasketballStrategy();
        }
    }
}