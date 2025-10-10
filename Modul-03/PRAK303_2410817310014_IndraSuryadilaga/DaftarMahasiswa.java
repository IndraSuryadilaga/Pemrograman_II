package modulTiga.PRAK203_2410817310014_IndraSuryadilaga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class DaftarMahasiswa {
	Scanner input = new Scanner(System.in);
	
	ArrayList<Mahasiswa> daftarMahasiswa = new ArrayList<>();
	
	//tambah Mahasiswa
	public void tambahMahasiswa(String nama, String nim) {
		//nim harus unik
		for (Mahasiswa m : daftarMahasiswa) {
			if (m.getNim().equals(nim)) {
				System.out.println("NIM sudah digunakan! Gagal menambahkan mahasiswa.");
                return;
			}
		}
		daftarMahasiswa.add(new Mahasiswa(nama, nim));
        System.out.println("Mahasiswa " + nama + " ditambahkan.");
	}
	
	//hapus mahasiswa berdasrkan nim
    public void hapusMahasiswa(String nim) {
        Iterator<Mahasiswa> iterator = daftarMahasiswa.iterator();
        while (iterator.hasNext()) {
            Mahasiswa m = iterator.next();
            if (m.getNim().equals(nim)) {
                iterator.remove();
                System.out.println("Mahasiswa dengan NIM " + nim + " dihapus.");
                return;
            }
        }
        System.out.println("Mahasiswa dengan NIM " + nim + " tidak ditemukan.");
    }
	
	//mencari mahasiswa menggunakan nim
	public void cariMahasiswa(String nim) {
		for (Mahasiswa m : daftarMahasiswa) {
			if (m.getNim().equals(nim)) {
				System.out.println("Data Mahasiswa Ditemukan:");
                System.out.println("NIM: " + m.getNim() + ", Nama: " + m.getNama());
                return;
			}
		}
		System.out.println("Mahasiswa dengan NIM " + nim + " tidak ditemukan.");
	}
	
    // Menampilkan semua data mahasiswa
    public void tampilkanSemua() {
        if (daftarMahasiswa.isEmpty()) {
            System.out.println("Daftar Mahasiswa kosong.");
            return;
        }
        System.out.println("Daftar Mahasiswa:");
        for (Mahasiswa m : daftarMahasiswa) {
            System.out.println("NIM: " + m.getNim() + ", Nama: " + m.getNama());
        }
    }
	
    // Menghapus semua data (saat keluar)
    public void hapusSemua() {
        daftarMahasiswa.clear();
    }
}
