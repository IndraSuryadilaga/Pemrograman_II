package modulTiga.PRAK201_2410817310014_IndraSuryadilaga;

import java.util.Random;

public class Dadu {
    private int mataDadu;

    public void acakNilai() {
        Random random = new Random();
        this.mataDadu = random.nextInt(6) + 1;
    }

    public int getMataDadu() {
        return mataDadu;
    }
}