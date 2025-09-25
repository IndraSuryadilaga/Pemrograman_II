package modulSatu;
import java.util.Scanner;

public class PRAK104_2410817310014_IndraSuryadilaga {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Tangan Abu: ");
		String[] tanganAbu = scanner.nextLine().split(" ");
		
		System.out.print("Tangan Bagas: ");
		String[] tanganBagas = scanner.nextLine().split(" ");
		
		int poinAbu = 0;
		int poinBagas = 0;
		
		for (int i = 0; i < 3; i++) {
            String pilihanAbu = tanganAbu[i];
            String pilihanBagas = tanganBagas[i];
            
            if ((pilihanAbu.equals("B") && pilihanBagas.equals("G")) ||
                (pilihanAbu.equals("G") && pilihanBagas.equals("K")) ||
                (pilihanAbu.equals("K") && pilihanBagas.equals("B"))) {
                poinAbu++;
            } 
           
            else if ((pilihanBagas.equals("B") && pilihanAbu.equals("G")) ||
                     (pilihanBagas.equals("G") && pilihanAbu.equals("K")) ||
                     (pilihanBagas.equals("K") && pilihanAbu.equals("B"))) {
                poinBagas++;
            }
        }
		
		if (poinAbu > poinBagas) {
            System.out.println("Abu");
        } else if (poinBagas > poinAbu) {
            System.out.println("Bagas");
        } else {
            System.out.println("Seri");
        }

        scanner.close();
	}

}
