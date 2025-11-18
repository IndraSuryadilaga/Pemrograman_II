package Modul_06.Model;

public class Mahasiswa {
	private int id;
	private String nama;
	private String nim;
	
	public Mahasiswa(int id, String nama, String nim) {
		this.id = id;
		this.nama = nama;
		this.nim = nim;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getNama() {
		return this.nama;
	}
	
	public String getNim() {
		return this.nim;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setNama(String nama) {
		this.nama = nama;
	}
	
	public void setNim(String nim) {
		this.nim = nim;
	}
}