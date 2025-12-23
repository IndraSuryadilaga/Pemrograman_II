## 📦 Package: `model`

Package ini berisi kelas-kelas representasi data (POJO - Plain Old Java Object) yang bertugas memetakan struktur tabel database menjadi objek Java.

<<<<<<< HEAD
Desain model ini menerapkan prinsip-prinsip **Object-Oriented Programming (OOP)** secara ketat untuk memastikan kode yang bersih, modular, dan mudah dikembangkan.

### 🛠 Konsep OOP yang Diterapkan
=======
## 🚀 Fitur Utama
>>>>>>> abadc5446d3933a6ecc60532ea2711327fba7e60

1.  **Inheritance (Pewarisan)**
    * Semua kelas model (`Team`, `Player`, `Match`, `Tournament`) mewarisi kelas induk `BaseModel`. Hal ini mencegah duplikasi kode untuk atribut umum seperti `id`.
2.  **Encapsulation (Enkapsulasi)**
    * Seluruh field dideklarasikan sebagai `private` (atau `protected` pada `BaseModel`) untuk melindungi akses data langsung.
    * Akses dan modifikasi data dilakukan melalui method `public` (Getter dan Setter).
3.  **Abstraction (Abstraksi)**
    * `BaseModel` dideklarasikan sebagai `abstract class`, menjadikannya kerangka dasar yang tidak bisa diinstansiasi sendiri, melainkan harus melalui kelas turunannya.
4.  **Polymorphism (Polimorfisme)**
    * **Method Overriding:** Setiap kelas meng-override method `toString()` untuk memberikan representasi string yang spesifik (misalnya menampilkan nama tim atau skor pertandingan).
    * **Constructor Overloading:** Setiap kelas memiliki beberapa variasi constructor (dengan ID untuk data dari DB, dan tanpa ID untuk pembuatan data baru).

---

### rincian Kelas (Class Breakdown)

#### 1. `BaseModel.java` (Abstract Parent)
Kelas dasar untuk semua entitas model.
* **Fungsi:** Menyediakan properti `id` dan memaksa implementasi method `toString()` pada kelas anak.
* **Penting:** Menggunakan modifier `protected` agar `id` dapat diakses langsung oleh kelas turunan namun tetap aman dari akses luar paket.

#### 2. `Team.java`
Merepresentasikan data Tim.
* **Atribut:** `name`, `logoPath`.
* **Fitur:** Digunakan untuk menyimpan informasi identitas tim yang berkompetisi.

<<<<<<< HEAD
#### 3. `Player.java`
Merepresentasikan data Pemain.
* **Atribut:** `teamId`, `name`, `jerseyNumber`, `position`.
* **Statistik Pertandingan:** Menyimpan `matchPoints` dan `matchFouls` untuk keperluan pelacakan performa dalam satu game.

#### 4. `Match.java`
Merepresentasikan sebuah Pertandingan dalam turnamen.
* **Atribut Data:** Menyimpan relasi antar tim (`homeTeam`, `awayTeam`), skor, jadwal, dan status pertandingan (`isFinished`).
* **State Timer (Auto-Save):** Memiliki field khusus (`currentQuarter`, `remainingSeconds`, `isTimerRunning`) untuk fitur penyimpanan status waktu pertandingan secara *real-time*.

#### 5. `Tournament.java`
Merepresentasikan data Turnamen/Liga.
* **Atribut:** `sportId`, `name`, `startDate`, `status`.
* **Fungsi:** Mengelompokkan pertandingan ke dalam satu event kompetisi tertentu.

## ⚖️ Package: `model.rules`

Package ini mengelola logika permainan yang spesifik untuk setiap cabang olahraga. Implementasi ini menggunakan dua Design Pattern utama: **Strategy Pattern** dan **Factory Pattern**.

Pendekatan ini memungkinkan aplikasi untuk mendukung aturan penilaian dan pelanggaran yang berbeda (misalnya Basket vs Badminton) tanpa mengubah kode inti aplikasi.

### 🧠 Design Patterns & Konsep OOP

1.  **Strategy Pattern (Pola Strategi)**
    * **Konsep:** Mendefinisikan serangkaian algoritma (aturan olahraga), membungkusnya dalam kelas terpisah, dan membuatnya dapat dipertukarkan.
    * **Implementasi:** `SportStrategy` bertindak sebagai *interface* umum. `BasketballStrategy` dan `BadmintonStrategy` adalah implementasi konkretnya. UI tidak perlu tahu detail aturan, cukup memanggil method dari interface.

2.  **Factory Pattern (Pola Pabrik)**
    * **Konsep:** Menyediakan antarmuka untuk membuat objek di superclass, tetapi membiarkan subclass atau method statis memutuskan kelas mana yang akan diinstansiasi.
    * **Implementasi:** `RuleFactory` bertugas membuat dan mengembalikan objek strategi yang tepat (`new BasketballStrategy()` atau `new BadmintonStrategy()`) berdasarkan string nama olahraga yang diberikan.

3.  **Polymorphism (Polimorfisme)**
    * Kode utama aplikasi hanya berinteraksi dengan interface `SportStrategy`. Ini memungkinkan aplikasi memperlakukan objek `BasketballStrategy` dan `BadmintonStrategy` secara seragam.

---

### rincian Kelas (Class Breakdown)

#### 1. `SportStrategy.java` (Interface)
Kontrak yang harus dipatuhi oleh setiap jenis olahraga baru.
* **Fungsi:** Menetapkan metode standar untuk:
    * `calculateNewScore`: Menghitung penambahan skor.
    * `getValidPointOptions`: Menentukan tombol poin apa saja yang muncul di UI (misal: Basket [1,2,3], Badminton [1]).
    * `isFoulOut`: Menentukan batas pelanggaran.

#### 2. `BasketballStrategy.java` (Concrete Implementation)
Aturan khusus untuk Bola Basket.
* **Skor:** Menambahkan poin sesuai input (1, 2, atau 3).
* **Foul:** Batas maksimal 5 pelanggaran (*Foul Out*).
* **Opsi Poin:** Mengembalikan list `[1, 2, 3]`.

#### 3. `BadmintonStrategy.java` (Concrete Implementation)
Aturan khusus untuk Bulutangkis.
* **Skor:** Selalu menambah +1 (inkremental), mengabaikan input poin dinamis.
* **Foul:** Batas 2 pelanggaran dianggap diskualifikasi (*Kartu Merah/Hitam*).
* **Opsi Poin:** Mengembalikan list `[1]`.

#### 4. `RuleFactory.java` (Factory Class)
Kelas utilitas statis untuk pembuatan objek aturan.
* **Method:** `getStrategy(String sportName)`.
* **Logika:** Menggunakan `switch-case` untuk mengecek nama olahraga dan mengembalikan strategi yang relevan.
* **Default:** Jika nama olahraga tidak dikenali atau null, secara default mengembalikan aturan Basket.

---

### 🚀 Keuntungan Arsitektur Ini
Jika di masa depan ingin menambahkan olahraga baru (misalnya: Voli atau Futsal), kita **tidak perlu mengubah kode yang sudah ada** di UI atau Database. Cukup buat class baru (misal `VolleyballStrategy`) yang mengimplementasikan `SportStrategy`, lalu daftarkan di `RuleFactory`. (Prinsip *Open/Closed Principle*).

## 🗄️ Package: `dao` (Data Access Object)

Package ini berfungsi sebagai lapisan perantara (layer) yang memisahkan logika bisnis aplikasi dari teknis akses database (SQLite). Semua operasi CRUD (*Create, Read, Update, Delete*) dan kueri kompleks dikelola di sini.

### 🛡️ Standar Keamanan & Performa

1.  **PreparedStatement**
    * Seluruh kueri SQL menggunakan `PreparedStatement` untuk menggantikan string concatenation. Ini **mencegah serangan SQL Injection** dan meningkatkan performa database karena kueri dikompilasi sebelumnya.
2.  **Try-with-Resources**
    * Koneksi database, statement, dan result set dibungkus dalam blok `try (...)`. Hal ini menjamin resource **ditutup secara otomatis** setelah selesai digunakan, mencegah kebocoran memori (*memory leaks*).
3.  **Transaction Management (ACID)**
    * Khusus pada pembuatan bracket turnamen (`MatchDao`), aplikasi menggunakan fitur transaksi (`conn.setAutoCommit(false)` dan `conn.commit()`).
    * **Fungsi:** Jika terjadi kesalahan saat membuat puluhan pertandingan secara massal, database akan melakukan *rollback* total sehingga tidak ada data sampah/setengah jadi yang tersimpan.

---

### 📂 Rincian Kelas (Class Breakdown)

#### 1. `DaoInterface.java` (Generic Interface)
Interface ini mendefinisikan kontrak standar operasi database.
* **Generics `<T>`:** Memungkinkan interface ini digunakan ulang oleh berbagai tipe model (`Player`, `Team`, `Tournament`) tanpa menulis ulang nama method, menerapkan prinsip **Abstraction** dan **Polymorphism**.
* **Method:** `getAll()`, `get(id)`, `add(T)`, `update(T)`, `delete(id)`.

#### 2. `TeamDao.java`, `PlayerDao.java`, `TournamentDao.java`
Implementasi standar dari `DaoInterface`.
* **Fungsi:** Menangani CRUD dasar untuk entitas Tim, Pemain, dan Turnamen.
* **Konversi Tipe Data:** `TournamentDao` menangani konversi antara `java.util.Date` (Java) dan `java.sql.Date` (SQL).

#### 3. `MatchDao.java` (Complex Logic)
Kelas DAO yang paling kompleks karena menangani logika turnamen dan statistik, bukan sekadar CRUD.
* **Bracket Generation (Batch Processing):** Menggunakan `addBatch()` dan `executeBatch()` untuk membuat struktur pertandingan turnamen (sistem gugur) secara massal dan efisien dalam satu transaksi database.
* **JOIN Queries:** Menggunakan `LEFT JOIN` untuk mengambil data pertandingan sekaligus dengan nama tim kandang dan tandang dalam satu kueri.
* **Statistics & Aggregation:** Method `getPlayerStatsByMatch` menggunakan fungsi agregat SQL (`SUM`, `COUNT`) untuk menghitung total poin dan pelanggaran pemain secara *real-time* dari tabel `match_events`.
* **Game Flow Logic:** Method `advanceWinnerToNextRound` otomatis memindahkan pemenang ke slot pertandingan di babak selanjutnya.

---

### 🔗 Hubungan Antar Tabel (Relational Logic)
Package DAO ini menangani relasi antar entitas seperti:
* **One-to-Many:** Satu `Team` memiliki banyak `Player` (`PlayerDao.getPlayersByTeam`).
* **Many-to-One:** Banyak `Match` terhubung ke satu `Tournament` (`MatchDao.getMatchesByTournament`).

## 🎮 Package: `controller`

Package ini bertindak sebagai "otak" aplikasi yang menghubungkan antarmuka pengguna (View/FXML) dengan logika bisnis dan data (Model & DAO). Setiap controller menangani interaksi pengguna, validasi input, dan pembaruan tampilan secara real-time.

### ⚙️ Fitur Utama & Pola Desain

1.  **Singleton Pattern (MainController)**
    * **Implementasi:** `MainController` menggunakan pola Singleton (`getInstance()`) untuk memastikan hanya ada satu pengelola navigasi utama dalam aplikasi.
    * **Manfaat:** Memudahkan controller lain (seperti `MatchOperatorController`) untuk memicu perpindahan halaman kembali ke Dashboard tanpa perlu membuat objek navigasi baru.

2.  **Integration with Strategy Pattern**
    * **Implementasi:** `MatchOperatorController` tidak menulis ulang logika skor. Ia memanggil `RuleFactory.getStrategy(sportName)` untuk mendapatkan aturan yang sesuai.
    * **Manfaat:** Controller ini menjadi *agnostic* (tidak peduli) terhadap jenis olahraga. Kode yang sama digunakan untuk Basket maupun Badminton, menjaga prinsip **DRY (Don't Repeat Yourself)**.

3.  **Dynamic UI Generation (Stream API)**
    * **Implementasi:** Pada `DashboardController`, visualisasi Bracket Turnamen digambar secara programatis menggunakan JavaFX nodes (`VBox`, `HBox`) berdasarkan data list pertandingan.
    * **Teknik:** Menggunakan Java Stream API (`Collectors.groupingBy`) untuk mengelompokkan pertandingan berdasarkan ronde secara efisien sebelum dirender.

4.  **Real-time State Management**
    * **Timeline Animation:** Menggunakan `javafx.animation.Timeline` untuk fitur *Countdown Timer* pertandingan yang akurat.
    * **Auto-Save:** Controller secara otomatis menyimpan status waktu dan skor ke database saat timer dipause atau halaman ditutup.

---

### 🕹️ Rincian Kelas (Class Breakdown)

#### 1. `MainController.java` (Navigation Hub)
Pengendali utama navigasi aplikasi.
* **Fungsi:** Mengganti tampilan di `StackPane` utama (`loadView()`) dan mengatur status tombol menu aktif.
* **Singleton:** Menyediakan akses global agar controller anak bisa meminta pergantian halaman.

#### 2. `DashboardController.java`
Halaman beranda aplikasi.
* **Visual Bracket:** Secara dinamis menggambar bagan pertandingan (sistem gugur) dari Babak Penyisihan hingga Final.
* **Statistik:** Menampilkan ringkasan total pertandingan dan jenis olahraga yang aktif.

#### 3. `MatchOperatorController.java` (The Game Engine)
Controller paling kompleks yang menangani jalannya pertandingan.
* **Timer & Quarter:** Mengelola logika waktu (Countdown), jeda istirahat, dan pergantian babak (Q1-Q4/Overtime).
* **Scoring:** Menangani input skor dan foul, lalu mengirimnya ke DAO.
* **Breakdown Skor:** Mencatat detail skor per quarter untuk keperluan statistik.

#### 4. `NewTournamentController.java`
Menangani pembuatan turnamen baru.
* **Transaction Logic:** Memicu `MatchDao.generateBracket()` untuk membuat struktur pertandingan secara otomatis setelah validasi input selesai.
* **Validasi:** Memastikan jumlah tim genap dan minimal 4 tim terpilih.

#### 5. `HistoryController.java`
Menampilkan riwayat pertandingan yang sudah selesai.
* **Reporting:** Menangani fitur **Export to PDF** dengan memanggil helper `PdfCreator`.
* **Master-Detail View:** Saat pengguna memilih baris pertandingan, tabel di bawahnya otomatis menampilkan detail statistik pemain dari match tersebut.

#### 6. `TeamController.java`
Manajemen data Master (CRUD).
* **Relasi Tim & Pemain:** Saat tim dipilih di tabel kiri, tabel kanan otomatis memuat daftar pemain dari tim tersebut (Konsep *Master-Detail*).

## 🛠️ Package: `helper`

Package ini berisi kumpulan **Utility Classes** yang menyediakan fungsi statis untuk mendukung operasional aplikasi. Kelas-kelas ini dirancang agar dapat digunakan ulang (*reusable*) di berbagai controller tanpa perlu instansiasi objek berulang kali.

### 🧩 Konsep Utama

1.  **Static Methods & Private Constructors**
    * Sebagian besar kelas di sini (seperti `AlertHelper`, `FormatterHelper`) memiliki *private constructor* untuk mencegah instansiasi. Semua metodenya bersifat `static`, sehingga bisa dipanggil langsung (contoh: `AlertHelper.showWarning(...)`).
2.  **External Library Integration**
    * Package ini menjadi jembatan antara aplikasi JavaFX dengan library eksternal seperti **SQLite JDBC** (Database) dan **iText 7** (PDF Generation).
3.  **User Experience (UX) Enhancements**
    * Helper ini meningkatkan pengalaman pengguna melalui umpan balik audio (`SoundHelper`) dan visualisasi pesan error yang konsisten (`AlertHelper`).

---

### 📂 Rincian Kelas (Class Breakdown)

#### 1. `DatabaseHelper.java`
Mengelola koneksi dan inisialisasi database.
* **Auto-Migration:** Fitur `createTablesIfNotExist` otomatis membuat tabel (Tournaments, Teams, Players, Matches, Events) jika file database belum ada. Ini membuat aplikasi bersifat *portable*.
* **Data Seeding:** Jika tabel kosong, aplikasi otomatis mengisi database dengan **Dummy Data** (Tim-tim IBL seperti Pelita Jaya, Satria Muda, dll) agar aplikasi siap didemokan langsung.
* **Teknologi:** `java.sql.DriverManager`, `JDBC`.

#### 2. `PdfCreator.java`
Menangani pembuatan laporan pertandingan digital.
* **Fungsi:** Mengambil objek `Match` dan list `Player`, lalu merendernya menjadi file PDF yang rapi berisi skor akhir dan tabel statistik pemain.
* **Teknologi:** Menggunakan library **iText 7 Kernel & Layout**.

#### 3. `SoundHelper.java`
Menangani efek suara (SFX) untuk meningkatkan interaktivitas.
* **Fitur:** Memuat aset audio sekali di awal (`static block`) untuk efisiensi memori, lalu memutarnya saat event tertentu terjadi.
    * `Score.mp3`: Diputar saat poin bertambah.
    * `Buzzer.mp3`: Diputar saat waktu 10 detik terakhir.
* **Teknologi:** `javafx.scene.media.AudioClip`.

#### 4. `AlertHelper.java`
Penyederhanaan tampilan dialog pesan.
* **Fungsi:** Membungkus logika `javafx.scene.control.Alert` agar Controller cukup memanggil satu baris kode untuk menampilkan pesan *Error*, *Warning*, atau *Information*.

#### 5. `FormatterHelper.java`
Helper untuk transformasi data.
* **Fungsi:** Mengubah data mentah `Map<Integer, Integer>` (skor per quarter) menjadi String yang mudah dibaca user, misal: `(18, 20, 15, 22)`.
* **Teknologi:** Memanfaatkan **Java Stream API** (`sorted`, `map`, `Collectors.joining`) untuk pemrosesan data yang ringkas dan ekspresif.
=======
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
>>>>>>> abadc5446d3933a6ecc60532ea2711327fba7e60
