package modulSatu;
import java.util.Scanner;

public class PRAK103_2410817310014_IndraSuryadilaga {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
        System.out.println("Input");
        int jumlahAngka = input.nextInt();
        int angkaAwal = input.nextInt();
        
        System.out.println("Output");
        int i = 0;
        
        do {
        	if (angkaAwal % 2 != 0) {
        		System.out.print(angkaAwal);
        		
        		i++;
        		
        		if (i < jumlahAngka) {
                    System.out.print(", ");
                }
        	}
        	angkaAwal++;	
        	
        } while (i < jumlahAngka);
        
        System.out.println();
        input.close();
	}
}