package Modul_07.Model;

public class Pelanggan {
	private int pelanggan_id;
	private String nama;
	private String email;
	private String telepon;
	
	public Pelanggan(int pelanggan_id, String nama, String email, String telepon) {
		this.pelanggan_id = pelanggan_id;
		this.nama = nama;
		this.email = email;
		this.telepon = telepon;
	}
	
	public void setPelanggan_id (int pelanggan_id) { this.pelanggan_id = pelanggan_id; }
	public void setNama (String nama) { this.nama = nama; }
	public void setEmail (String email) { this.email = email; }
	public void setTelepon(String telepon) { this.telepon = telepon;}
	
	public int getPelanggan_id() { return this.pelanggan_id; }
	public String getNama() { return this.nama; }
	public String getEmail() { return this.email; }
	public String getTelepon() { return this.telepon; }
}