package modulSatu;
import java.util.Scanner;

public class PRAK101_2410817310014_IndraSuryadilaga {

    public static void main(String[] args) {
    	Scanner input = new Scanner(System.in);

        // --- Input Nama dan Tempat Lahir ---
        System.out.print("Masukkan Nama Lengkap: ");
        String namaLengkap = input.nextLine();

        System.out.print("Masukkan Tempat Lahir: ");
        String tempatLahir = input.nextLine();

        // --- Variabel untuk Data Kelahiran ---
        int tanggalLahir;
        int bulanLahir;
        int tahunLahir;
        boolean tanggalValid = false;

        // --- Perulangan untuk Validasi Data Kelahiran ---
        do {
            System.out.print("Masukkan Tanggal Lahir: ");
            tanggalLahir = input.nextInt();

            System.out.print("Masukkan Bulan Lahir: ");
            bulanLahir = input.nextInt();

            System.out.print("Masukkan Tahun Lahir: ");
            tahunLahir = input.nextInt();

            // Memeriksa validitas dasar bulan dan tahun terlebih dahulu
            if (bulanLahir < 1 || bulanLahir > 12 || tahunLahir <= 0) {
                System.out.println("Bulan atau Tahun tidak valid. Silakan masukkan kembali data kelahiran Anda.");
                continue;
            }

            // Menentukan jumlah hari maksimum dalam sebulan (termasuk tahun kabisat)
            int maxTanggal;
            if (bulanLahir == 2) {
                // Cek tahun kabisat (leap year)
                boolean isKabisat = (tahunLahir % 4 == 0 && tahunLahir % 100 != 0) || (tahunLahir % 400 == 0);
                maxTanggal = isKabisat ? 29 : 28;
            } else if (bulanLahir == 4 || bulanLahir == 6 || bulanLahir == 9 || bulanLahir == 11) {
                maxTanggal = 30;
            } else {
                maxTanggal = 31;
            }

            // Memeriksa apakah tanggal yang dimasukkan sesuai dengan rentang bulan dan tahun
            if (tanggalLahir >= 1 && tanggalLahir <= maxTanggal) {
                tanggalValid = true;
            } else {
                System.out.println("=> ERROR: Tanggal " + tanggalLahir + " tidak ada pada bulan ke-" + bulanLahir + " tahun " + tahunLahir + ". Silakan masukkan kembali data kelahiran Anda.");
            }

        } while (!tanggalValid);


        // --- Validasi Input Tinggi dan Berat Badan ---
        int tinggiBadan;
        do {
            System.out.print("Masukkan Tinggi Badan: ");
            tinggiBadan = input.nextInt();
            if (tinggiBadan <= 0) {
                System.out.println("=> ERROR: Tinggi badan harus angka positif.");
            }
        } while (tinggiBadan <= 0);

        double beratBadan;
        do {
            System.out.print("Masukkan Berat Badan: ");
            beratBadan = input.nextDouble();
            if (beratBadan <= 0) {
                System.out.println("=> ERROR: Berat badan harus angka positif.");
            }
        } while (beratBadan <= 0);

        // --- Proses dan Output ---
        String[] namaBulanArray = {
            "", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };
        
        String namaBulan = namaBulanArray[bulanLahir];
        
        System.out.println("Nama Lengkap " + namaLengkap + ", Lahir di " + tempatLahir + " pada Tanggal " + tanggalLahir + " " + namaBulan + " " + tahunLahir);
        System.out.println("Tinggi Badan " + tinggiBadan + " cm dan Berat Badan " + beratBadan + " kilogram");
        
        System.out.println();
        input.close();
    }
}