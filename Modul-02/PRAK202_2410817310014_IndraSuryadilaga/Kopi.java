package PRAK202_2410817310014_IndraSuryadilaga;

public class Kopi {
    // Atribut
    String namaKopi;
    String ukuran;
    double harga;
    private String pembeli;

    // Method untuk menampilkan info kopi
    public void info() {
        System.out.println("Nama Kopi: " + namaKopi);
        System.out.println("Ukuran: " + ukuran);
        System.out.println("Harga: Rp. " + harga);
    }

    // Setter untuk pembeli
    public void setPembeli(String pembeli) {
        this.pembeli = pembeli;
    }

    // Getter untuk pembeli
    public String getPembeli() {
        return pembeli;
    }

    // Method untuk menghitung pajak 11%
    public double getPajak() {
        return harga * 0.11;
    }
}
