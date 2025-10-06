package PRAK201_2410817310014_IndraSuryadilaga;

//Class Buah
class Main {
    // Atribut
	private String nama;
	private double beratPerUnit;
	private double hargaPerUnit;
	private double jumlahBeli; 

 // Constructor
 public Main(String nama, double beratPerUnit, double hargaPerUnit, double jumlahBeli) {
     this.nama = nama;
     this.beratPerUnit = beratPerUnit;
     this.hargaPerUnit = hargaPerUnit;
     this.jumlahBeli = jumlahBeli;
 }

 // Method untuk menghitung harga sebelum diskon
 public double hitungHargaSebelumDiskon() {
     double jumlahUnit = jumlahBeli / beratPerUnit;
     return jumlahUnit * hargaPerUnit;
 }

 // Method untuk menghitung total diskon
 public double hitungDiskon() {
     // Diskon 2% setiap 4kg, Artinya (jumlahBeli / 4) * 2%
     double diskonPersen = (int)(jumlahBeli / 4) * 0.02;
     return hitungHargaSebelumDiskon() * diskonPersen;
 }

 // Method untuk menghitung harga setelah diskon
 public double hitungHargaSetelahDiskon() {
     return hitungHargaSebelumDiskon() - hitungDiskon();
 }

 // Method untuk menampilkan semua informasi
 public void tampilkanInfo() {
     System.out.println("Nama Buah: " + nama);
     System.out.println("Berat: " + beratPerUnit);
     System.out.println("Harga: " + hargaPerUnit);
     System.out.println("Jumlah Beli: " + jumlahBeli + "kg");
     System.out.printf("Harga Sebelum Diskon: Rp%.2f%n", hitungHargaSebelumDiskon());
     System.out.printf("Total Diskon: Rp%.2f%n", hitungDiskon());
     System.out.printf("Harga Setelah Diskon: Rp%.2f%n", hitungHargaSetelahDiskon());
     System.out.println();
 }
}
