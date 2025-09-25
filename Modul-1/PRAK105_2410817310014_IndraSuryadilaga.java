package modulSatu;
import java.util.Scanner;

public class PRAK105_2410817310014_IndraSuryadilaga {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Masukkan jari-jari: ");
		double jariJari = scanner.nextDouble();
		
		System.out.print("Masukkan tinggi: ");
		double tinggi = scanner.nextDouble();
		
		double volume = jariJari * jariJari * tinggi * 3.14;
		String volumeFormat = String.format("%.3f", volume);
		
		System.out.print("Volume tabung dengan jari-jari " + jariJari + " cm dan tinggi " + tinggi + " cm adalah " + volumeFormat + " m3");
	}

}
