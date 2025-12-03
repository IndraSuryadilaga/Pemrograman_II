package Modul_07.Model;

public class Buku {
	private int buku_id;
	private String judul;
	private String penulis;
	private double harga;
	private int stok;
	
	public Buku (int buku_id, String judul, String penulis, double harga, int stock) {
		this.buku_id = buku_id;
		this.judul = judul;
		this.penulis = penulis;
		this.harga = harga;
		this.stok = stock;
	}
	
	public void setBukuId(int buku_id) { this.buku_id = buku_id; }
	public void setJudul(String judul) { this.judul = judul; }
	public void setPenulis(String penulis) { this.penulis = penulis; }
	public void setHarga(double harga) { this.harga = harga; }
	public void setStok(int stok) { this.stok = stok; }
	
	public int getBuku_id() { return this.buku_id; }
	public String getJudul() { return this.judul; }
	public String getPenulis() { return this.penulis; }
	public double getHarga() { return this.harga; }
	public int getStok() { return this.stok; }
}
