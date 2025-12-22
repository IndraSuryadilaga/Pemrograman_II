# 🏆 SportaApp - Sistem Manajemen Pertandingan Olahraga

**SportaApp** adalah aplikasi desktop berbasis **JavaFX** yang dirancang untuk membantu penyelenggara turnamen olahraga (Basket & Badminton) dalam mengelola pertandingan, mulai dari pendaftaran tim, pembuatan bagan pertandingan (bracket), hingga pencatatan skor secara real-time.

## 🚀 Fitur Utama

* **Multi-Cabang Olahraga:** Mendukung aturan dan sistem skor untuk **Bola Basket** 🏀 dan **Bulutangkis** 🏸.
* **Manajemen Turnamen:** Membuat turnamen baru dan menyimpan riwayat turnamen lama.
* **Automatic Bracket Generation:** Secara otomatis membuat bagan pertandingan (sistem gugur) berdasarkan tim yang dipilih.
* **Live Match Operator:**
    * Papan skor digital.
    * Timer pertandingan (Countdown) dengan fitur Pause/Resume.
    * Pencatatan Quarter/Set otomatis.
    * Pencatatan pelanggaran (Foul).
    * **Sound Effects:** Efek suara saat mencetak poin dan Buzzer saat waktu habis.
* **Database Lokal:** Menggunakan **SQLite** untuk penyimpanan data yang ringan dan otomatis (Auto-Save).
* **Statistik Pertandingan:** Melihat detail skor per quarter dan total skor akhir.
* **Ekspor Data:** Fitur untuk mencetak laporan pertandingan (PDF).

## 🛠️ Teknologi yang Digunakan

* **Bahasa Pemrograman:** Java (JDK 21)
* **Framework UI:** JavaFX 21
* **Build Tool:** Maven
* **Database:** SQLite (`sqlite-jdbc`)
* **Library Tambahan:**
    * `iText7` (Untuk generate PDF)
    * `JavaFX Media` (Untuk efek suara)

## 📂 Struktur Project

Aplikasi ini menggunakan arsitektur **MVC (Model-View-Controller)** dengan pola **DAO (Data Access Object)**:

```text
SportaApp/
├── src/main/java/
│   ├── application/    # Entry point (Main.java, Launcher.java)
│   ├── controller/     # Logika UI (Dashboard, MatchOperator, dll)
│   ├── dao/            # Akses Database (TeamDao, MatchDao, dll)
│   ├── helper/         # Utilitas (DatabaseHelper, SoundHelper, AlertHelper)
│   ├── model/          # Representasi Data & Aturan Game (Strategy Pattern)
│   └── module-info.java
└── src/main/resources/
    ├── sounds/         # Aset audio (score.wav, buzzer.wav)
    ├── view/           # File FXML dan CSS
    └── images/         # Logo tim/aplikasi
