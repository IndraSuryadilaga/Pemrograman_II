package PRAK201_2410817310014_IndraSuryadilaga;

public class Buah{

	public static void main(String[] args) {
		Main apel = new Main("Apel", 0.4, 7000.0, 40.0);
		Main mangga = new Main("mangga", 0.2, 3500.0, 15.0);
		Main alpukat = new Main("alpukat", 0.25, 10000.0, 12.0);
		
		apel.tampilkanInfo();
		mangga.tampilkanInfo();
		alpukat.tampilkanInfo();
	}

}
