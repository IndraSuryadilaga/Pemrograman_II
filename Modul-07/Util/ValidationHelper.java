package Modul_07.Util;

import java.util.regex.Pattern;

public class ValidationHelper {

    // Validasi field tidak boleh kosong
    public static boolean isEmpty(String... texts) {
        for (String text : texts) {
            if (text == null || text.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Validasi Email dengan Regex
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    // Validasi Telepon
    public static boolean isValidTelepon(String telepon) {
        if (telepon == null || telepon.isEmpty()) return false;
        // Hapus spasi atau tanda strip jika ada
        String cleaned = telepon.replaceAll("[\\s-]", "");
        return cleaned.matches("^[0-9]+$") && cleaned.length() >= 8;
    }

    // Validasi Angka (Cek apakah String adalah angka valid)
    public static boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Validasi Angka Negatif
    public static boolean isNegative(double number) {
        return number < 0;
    }

    // Validasi Tanggal (YYYY-MM-DD)
    public static boolean isValidTanggal(String tanggal) {
        if (tanggal == null || !tanggal.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return false;
        }
        
        try {
            String[] parts = tanggal.split("-");
            int tahun = Integer.parseInt(parts[0]);
            int bulan = Integer.parseInt(parts[1]);
            int hari = Integer.parseInt(parts[2]);

            if (bulan < 1 || bulan > 12) return false;
            if (hari < 1 || hari > 31) return false;

            // Cek sederhana hari per bulan
            int maxHari = 31;
            if (bulan == 4 || bulan == 6 || bulan == 9 || bulan == 11) {
                maxHari = 30;
            } else if (bulan == 2) {
                boolean isKabisat = (tahun % 4 == 0 && tahun % 100 != 0) || (tahun % 400 == 0);
                maxHari = isKabisat ? 29 : 28;
            }
            return hari <= maxHari;

        } catch (Exception e) {
            return false;
        }
    }
}