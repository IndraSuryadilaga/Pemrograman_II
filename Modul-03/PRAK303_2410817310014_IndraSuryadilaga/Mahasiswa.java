package modulTiga.PRAK203_2410817310014_IndraSuryadilaga;

public class Mahasiswa {
	//atribut
	private String nama;
	private String nim;
	
	//constructor
	Mahasiswa(String nama, String nim) {
		this.nama = nama;
		this.nim = nim;
	}
	
	
	//getter
	public String getNama() {
		return this.nama;
	}
	
	public String getNim() {
		return this.nim;
	}
}
