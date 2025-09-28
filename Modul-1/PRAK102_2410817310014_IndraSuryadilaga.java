package modulSatu;
import java.util.Scanner;

public class PRAK102_2410817310014_IndraSuryadilaga {

	public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("Input");
        int angkaAwal = input.nextInt();
        
        System.out.println("Output");
        int i = 0;

        while (i <= 10) {
            
            if (angkaAwal % 5 == 0) {
                int hasil = (angkaAwal / 5) - 1;
                System.out.print(hasil);
            } else {
                System.out.print(angkaAwal);
            }

            if (i < 10) {
                System.out.print(", ");
            }

            angkaAwal++;
            i++;
        }
        
        System.out.println();
        input.close();
	}

}
