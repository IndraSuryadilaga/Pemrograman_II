package modulTiga.PRAK202_2410817310014_IndraSuryadilaga;
import java.util.*;

public class Main {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		int jumlahNegara;
		boolean jumlahValid = false;
		LinkedList<Negara> daftarNegara = new LinkedList<>();
		HashMap<Integer, String> namaBulan = new HashMap<>();

		namaBulan.put(1, "Januari");
		namaBulan.put(2, "Febuari");
		namaBulan.put(3, "Maret");
		namaBulan.put(4, "April");
		namaBulan.put(5, "Mei");
		namaBulan.put(6, "Juni");
		namaBulan.put(7, "Juli");
		namaBulan.put(8, "Agustus");
		namaBulan.put(9, "September");
		namaBulan.put(10, "Oktober");
		namaBulan.put(11, "November");
		namaBulan.put(12, "December");
		
		//input jumlah negara dengan validasi jumlah negara harus lebih dari 0
		do {
			jumlahNegara = input.nextInt();
			if (jumlahNegara < 1) {
				System.out.println("jumlah negara yang anda masukan tidak valid,jumlah negara harus lebih dari 0");
				continue;
			} else {
				jumlahValid = true;
			}
			input.nextLine();
		}while (!jumlahValid);
		
		//input data lengkap negara
		for (int i = 0; i < jumlahNegara; i++) {
			//input nama, jenis kepemimpinan, dan nama pemeimpin
            String nama = input.nextLine();
            String jenisKepemimpinan = input.nextLine();
            String namaPemimpin = input.nextLine();
            
            //input tanggal, bulan, dan tahun kemerdekaan
            if (jenisKepemimpinan.equalsIgnoreCase("monarki")) {
                //Negara monarki tidak memiliki tanggal kemerdekaan
                daftarNegara.add(new Negara(nama, jenisKepemimpinan, namaPemimpin));
            } else {
                //Perulangan untuk Validasi Data Kemerdekaan
                int tglKemerdekaan;
                int blnKemerdekaan;
                int thnKemerdekaan; 
                boolean tanggalValid = false;
                
                do {
                    tglKemerdekaan = input.nextInt();
                    blnKemerdekaan = input.nextInt();
                    thnKemerdekaan = input.nextInt();
                    input.nextLine();

                    if (blnKemerdekaan < 1 || blnKemerdekaan > 12 || thnKemerdekaan <= 0) {
                        System.out.println("Bulan atau Tahun tidak valid. Silakan masukkan kembali tahun kemerdekaan.");
                        continue;
                    }

                    // Menentukan jumlah hari maksimum dalam sebulan (termasuk tahun kabisat)
                    int maxTanggal;
                    if (blnKemerdekaan == 2) {
                        // Cek tahun kabisat (leap year)
                        boolean isKabisat = (thnKemerdekaan % 4 == 0 && thnKemerdekaan % 100 != 0) || (thnKemerdekaan % 400 == 0);
                        maxTanggal = isKabisat ? 29 : 28;
                    } else if (blnKemerdekaan == 4 || blnKemerdekaan == 6 || blnKemerdekaan == 9 || blnKemerdekaan == 11) {
                        maxTanggal = 30;
                    } else {
                        maxTanggal = 31;
                    }

                    // Memeriksa apakah tanggal yang dimasukkan sesuai dengan rentang bulan dan tahun
                    if (tglKemerdekaan >= 1 && tglKemerdekaan <= maxTanggal) {
                        tanggalValid = true;
                    } else {
                        System.out.println("=> ERROR: Tanggal " + tglKemerdekaan + " tidak ada pada bulan ke-" + blnKemerdekaan + " tahun " + thnKemerdekaan + ". Silakan masukkan kembali data kemerdekaan.");
                    }

                } while (!tanggalValid);

                daftarNegara.add(new Negara(nama, jenisKepemimpinan, namaPemimpin, tglKemerdekaan, blnKemerdekaan, thnKemerdekaan));
            }
		}
		
		//output data negara dengan perulangan.
		System.out.println();
        for (Negara negara : daftarNegara) {
            if (negara.getJenisKepemimpinan().equalsIgnoreCase("monarki")) {
                negara.TampilkanInfo("");
            } else {
                String bulanNama = namaBulan.get(negara.getBlnKemerdekaan());
                negara.TampilkanInfo(bulanNama);
            }
        }
        
        input.close();
	}
}
