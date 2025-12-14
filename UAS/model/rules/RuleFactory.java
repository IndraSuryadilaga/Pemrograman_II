package model.rules;

public class RuleFactory {
    
    public static SportStrategy getStrategy(String sportName) {
        if (sportName == null) return new BasketballStrategy(); // Default

        switch (sportName.toLowerCase()) {
            case "basketball":
            case "basket":
                return new BasketballStrategy();
                
            case "badminton":
            case "bulutangkis":
                return new BadmintonStrategy();
                
            // Penambahan case baru jika ingin menambahkan rules pertandian lainnya
            
            default:
                return new BasketballStrategy();
        }
    }
}