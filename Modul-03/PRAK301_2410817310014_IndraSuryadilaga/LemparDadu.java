package modulTiga.PRAK201_2410817310014_IndraSuryadilaga;

import java.util.LinkedList;
import java.util.Scanner;

public class LemparDadu {
    private LinkedList<Dadu> daftarDadu = new LinkedList<>();
    private int jumlahDadu;
    private int totalNilai;

    public void inputJumlahDadu() {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.print("Masukkan jumlah dadu: ");
            jumlahDadu = input.nextInt();
            if (jumlahDadu > 0) break;
            System.out.println("Jumlah dadu harus lebih dari 0!");
        }
        input.close();
    }

    public void lemparDadu() {
        totalNilai = 0;
        daftarDadu.clear();
        for (int i = 0; i < jumlahDadu; i++) {
            Dadu d = new Dadu();
            d.acakNilai();
            daftarDadu.add(d);
            System.out.println("Dadu ke-" + (i + 1) + " bernilai " + d.getMataDadu());
            totalNilai += d.getMataDadu();
        }
    }

    public void tampilkanHasil() {
        System.out.println("Total nilai dadu keseluruhan: " + totalNilai);
    }
}