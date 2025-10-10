package modulTiga.PRAK202_2410817310014_IndraSuryadilaga;

public class Negara {
	//atribut
	private String nama;
	private String jenisKepemimpinan;
	private String namaPemimpin;
	private int tglKemerdekaan;
	private int blnKemerdekaan;
	private int thnKemerdekaan;

	//constructor negara dengan kemerdekaan
	public Negara(String nama, String jenisKepemimpinan, String namaPemimpin, int tglKemerdekaan, int blnKemerdekaan, int thnKemerdekaan) {
		this.nama = nama;
		this.jenisKepemimpinan = jenisKepemimpinan;
		this.namaPemimpin = namaPemimpin;
		this.tglKemerdekaan = tglKemerdekaan;
		this.blnKemerdekaan = blnKemerdekaan;
		this.thnKemerdekaan = thnKemerdekaan;
	}
	
	//constructor negara tanpa kemerdekaan
	public Negara(String nama, String jenisKepemimpinan, String namaPemimpin) {
		this.nama = nama;
		this.jenisKepemimpinan = jenisKepemimpinan;
		this.namaPemimpin = namaPemimpin;
	}
	
	//Getter
	public String getNama() {
		return nama;
	}

	public String getJenisKepemimpinan() {
		return jenisKepemimpinan;
	}
	
	public String getNamaPemimpin() {
		return namaPemimpin;
	}
	
	public int getTglKemerdekaan() {
		return tglKemerdekaan;
	}
	
	public int getBlnKemerdekaan() {
		return blnKemerdekaan;
	}

	public int getThnKemerdekaan() {
		return thnKemerdekaan;
	}
	
	//method menampilkan informasi negara
	public void TampilkanInfo(String namaBulan) {
		System.out.println("Negara " + nama + " mempunyai " 
                + (jenisKepemimpinan.equalsIgnoreCase("presiden") ? "Presiden" : "Raja")
                + " bernama " + namaPemimpin);

        if (!jenisKepemimpinan.equalsIgnoreCase("monarki")) {
            System.out.println("Deklarasi Kemerdekaan pada Tanggal " 
                    + tglKemerdekaan + " " + namaBulan + " " + thnKemerdekaan);
        }
        System.out.println();
	}
	
}
