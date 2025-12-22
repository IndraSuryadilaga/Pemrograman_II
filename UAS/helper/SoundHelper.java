package helper;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class SoundHelper {
    
    private static AudioClip scoreSound;
    private static AudioClip buzzerSound;

    static {
        try {
            scoreSound = loadClip("/sounds/Score.mp3");
            buzzerSound = loadClip("/sounds/Buzzer.mp3");
        } catch (Exception e) {
            System.err.println("Gagal memuat aset suara: " + e.getMessage());
        }
    }

    private static AudioClip loadClip(String path) {
        URL resource = SoundHelper.class.getResource(path);
        if (resource == null) {
            System.err.println("File suara tidak ditemukan: " + path);
            return null;
        }
        return new AudioClip(resource.toExternalForm());
    }

    // Method publik untuk memutar suara
    public static void playScore() {
        if (scoreSound != null) scoreSound.play();
    }

    public static void playBuzzer() {
        if (buzzerSound != null) buzzerSound.play();
    }
}