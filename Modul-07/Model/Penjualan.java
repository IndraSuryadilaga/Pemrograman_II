package Modul_07.Model;

public class Penjualan {
	private int penjualan_id;
	private int jumlah;
	private double total_harga;
	private String tanggal;
	private int pelanggan_id;
	private int buku_id;
	
	public Penjualan(int penjualan_id, int jumlah, double total_harga, String tanggal, int pelanggan_id, int buku_id) {
		this.penjualan_id = penjualan_id;
		this.jumlah = jumlah;
		this.total_harga = total_harga;
		this.tanggal = tanggal;
		this.pelanggan_id = pelanggan_id;
		this.buku_id = buku_id;
	}
	
	public int getPenjualan_id() { return penjualan_id; }
    public void setPenjualan_id(int penjualanId) { this.penjualan_id = penjualan_id; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public double getTotal_harga() { return total_harga; }
    public void setTotal_harga(double totalHarga) { this.total_harga = total_harga; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public int getPelanggan_id() { return pelanggan_id; }
    public void setPelanggan_id(int pelangganId) { this.pelanggan_id = pelanggan_id; }

    public int getBuku_id() { return buku_id; }
    public void setBuku_id(int bukuId) { this.buku_id = buku_id; }
}
