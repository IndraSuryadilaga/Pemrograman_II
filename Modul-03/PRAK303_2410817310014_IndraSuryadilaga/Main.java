package modulTiga.PRAK203_2410817310014_IndraSuryadilaga;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
        DaftarMahasiswa daftar = new DaftarMahasiswa();
        int pilihan;
        
        do {
            System.out.println("Menu:");
            System.out.println("1. Tambah Mahasiswa");
            System.out.println("2. Hapus Mahasiswa berdasarkan NIM");
            System.out.println("3. Cari Mahasiswa berdasarkan NIM");
            System.out.println("4. Tampilkan Daftar Mahasiswa");
            System.out.println("0. Keluar");
            System.out.print("Pilihan: ");
            pilihan = input.nextInt();
            input.nextLine(); // buang newline

            switch (pilihan) {
                case 1:
                    System.out.print("Masukkan Nama Mahasiswa: ");
                    String nama = input.nextLine();
                    System.out.print("Masukkan NIM Mahasiswa (harus unik): ");
                    String nim = input.nextLine();
                    daftar.tambahMahasiswa(nama, nim);
                    break;

                case 2:
                    System.out.print("Masukkan NIM Mahasiswa yang akan dihapus: ");
                    nim = input.nextLine();
                    daftar.hapusMahasiswa(nim);
                    break;

                case 3:
                    System.out.print("Masukkan NIM Mahasiswa yang dicari: ");
                    nim = input.nextLine();
                    daftar.cariMahasiswa(nim);
                    break;

                case 4:
                    daftar.tampilkanSemua();
                    break;

                case 0:
                    daftar.hapusSemua();
                    System.out.println("Terima kasih!");
                    break;

                default:
                    System.out.println("Pilihan tidak valid.\n");
                    break;
            }
            
        } while (pilihan != 0);

        input.close();
	}
}
