# The Last Commit
![The Last Commit Banner](app/src/main/resources/images/banner-the-last-commit.png)

<div align="center">

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-blue?style=for-the-badge&logo=java)](https://openjfx.io/)
[![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)](https://sqlite.org/)
[![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)
[![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)](https://git-scm.com/)

</div>

---

### 👥 Identitas Proyek Lab PBO 2026
<p align="justify">
  Proyek ini disusun oleh <b>Kelompok 14</b> Laboratorium Pemrograman Berorientasi Objek (PBO) Program Studi Sistem Informasi, Universitas Hasanuddin, di bawah bimbingan langsung Asisten Laboratorium Pembimbing kami.
</p>

#### Asisten Laboratorium Pembimbing:
*   **Muhammad Fadel Aryasatya Makkulau** (NIM: H071241071)

#### Anggota Kelompok 14 OOP:
| No | Nama | NIM | Kontribusi & Peran Spesifik |
| :-: | :--- | :-: | :--- |
| 1 | **Muh. Rafly Nurramadhan** | H071251079 | Lead Programmer, Visual Novel Designer, Setup Project, Refactoring, & Debugging |
| 2 | **Andika Rahman** | H071251061 | Battle System & Item Management (Shop & Inventory) |
| 3 | **Imam Arief Rachmat** | H071251093 | Login, Register, & Character Selection UI |

---

### 🎮 Ringkasan Proyek
<p align="justify">
  <b>The Last Commit</b> adalah game bertema <b>Hiburan (Game RPG Turn-Based Visual Novel)</b> premium yang dikembangkan menggunakan <b>JavaFX</b> dan <b>SQLite</b> sebagai proyek akhir untuk memenuhi tugas Laboratorium Pemrograman Berorientasi Objek (PBO) 2026. Game ini mengambil latar cerita dramatis dan jenaka tentang seorang mahasiswa sistem informasi yang berjuang melakukan <i>git commit</i> terakhir untuk proyek akhirnya beberapa menit sebelum tenggat waktu tengah malam (<i>deadline</i>). Mahasiswa tersebut harus bertarung menembus kumpulan monster yang mewakili berbagai error kompilasi, bug, dan hambatan teknis lainnya, sebelum menghadapi musuh utama yang merupakan biang keladi kegagalan sistem: <b>Imam Voldigoad (Fatal Bug)</b>.
</p>

---

### 🏛️ Penerapan 4 Pilar OOP (PBO Core)
Proyek ini mengimplementasikan empat pilar utama Pemrograman Berorientasi Objek secara eksplisit dan rapi di dalam model entitas:

#### 1. Abstraction (Abstraksi)
*   **Interface `Combatant`**: Menyediakan kontrak abstrak murni untuk semua entitas yang dapat berpartisipasi dalam pertarungan (`getName()`, `getCurrentHp()`, `getMaxHp()`, `isDead()`, `takeDamage()`).
*   **Abstract Class `GameCharacter` & `Hero`**: Kelas abstrak yang menjadi cetak biru (*blueprint*) bagi seluruh petarung. Kelas ini tidak dapat diinstansiasi secara langsung (abstract) dan memaksa turunan konkretnya untuk melengkapi metode khusus seperti `getRoleDescription()`.

#### 2. Inheritance (Pewarisan)
*   **Hirarki Karakter Pemain**:
    `Combatant` (Interface) ➔ `GameCharacter` (Abstract) ➔ `Hero` (Abstract) ➔ `KatagiriHero` & `KyotakaHero` (Concrete Subclass).
*   **Hirarki Musuh**:
    `GameCharacter` (Abstract) ➔ `Enemy` (Concrete Subclass) ➔ `BossEnemy` (Concrete Subclass).
    <p align="justify">Pewarisan ini memastikan penggunaan kode yang bersih, modular, dapat digunakan kembali (<i>reusable</i>), dan menghindari duplikasi kode yang tidak perlu (<i>boilerplate</i>).</p>

#### 3. Polymorphism (Polimorfisme)
*   **Method Overriding**: 
    *   Metode `takeDamage(damage)` di-override secara spesifik oleh `Hero` untuk menyertakan perhitungan mitigasi Defense, sedangkan `Enemy` menguranginya secara langsung.
    *   Metode `getRoleDescription()` di-override oleh `KatagiriHero` (kemampuan Mage) dan `KyotakaHero` (kemampuan Warrior) untuk mengembalikan peran unik masing-masing.
*   **Polymorphic References**:
    <p align="justify">Sistem pertempuran (<i>CombatServiceImpl</i>) menerima parameter bertipe <i>Combatant</i> atau <i>GameCharacter</i>. Di bawah kap (<i>under the hood</i>), Java secara dinamis mengikat (<i>dynamic binding</i>) versi metode yang sesuai dari subclass (apakah Hero, Enemy biasa, atau Boss Voldigoad) pada saat runtime.</p>

#### 4. Encapsulation (Enkapsulasi)
*   Deklarasi properti sensitif seperti `currentHp`, `gold`, dan `currentResource` menggunakan akses `protected` atau `private` untuk melindunginya dari modifikasi langsung di luar lingkup kelas.
*   Penyediaan metode akses publik (*Getter/Setter*) yang aman yang dilengkapi dengan validasi ketat (misal: *clamping* nilai HP agar tidak melampaui batas maksimum atau kurang dari nol).

---

### 🧵 Implementasi Concurrency & Thread-Safety (Nilai Plus)
<p align="justify">
  Aplikasi menerapkan konsep pemrograman asinkron dan multi-threading secara aman untuk menjaga fungsionalitas visual game agar tetap berjalan lancar tanpa mengalami <i>screen freezing</i> (tidak responsif).
</p>

*   **Asynchronous Timeline Threading**: <p align="justify">Logika waktu mundur pertempuran (5-second turn timer), perhitungan durasi cooldown skill hero, serta interval animasi perpindahan giliran musuh diimplementasikan menggunakan pemrosesan asinkron aseli JavaFX. Hal ini memastikan game dapat memproses waktu berjalan di latar belakang secara <i>real-time</i> tanpa memblokir benang utama perenderan visual.</p>
*   **Thread-Safe UI Synchronization**: <p align="justify">Saat terjadi penulisan atau pembaruan database SQLite asinkron (misalnya penghapusan progres saat Game Over atau penyimpanan data Wave saat Victory), sistem memanggil callback <b><code>Platform.runLater()</code></b>. Perintah ini berfungsi menyinkronkan kembali pembaruan visual ke <i>JavaFX Application Thread</i> utama secara aman, menghindarkan aplikasi dari error tabrakan thread (<code>IllegalStateException: Not on FX application thread</code>).</p>
*   **Thread Suspended & Resume Control (Pause System)**: <p align="justify">Mekanisme jeda (Pause Menu) permainan menghentikan sementara seluruh aktivitas thread visual dan timer pertempuran menggunakan perintah <code>Timeline.pause()</code> dan dapat melanjutkannya kembali dengan <code>Timeline.play()</code> secara aman tanpa risiko kebocoran memori (<i>memory leaks</i>).</p>

---

### ⚙️ Fitur Utama Aplikasi
*   **Arsitektur Dekopling Modular (Interface Segregation)**: <p align="justify">Controller pertempuran didekopling dari View menggunakan <i>BattleViewBridge</i>, serta logika taktis combat diisolasi menggunakan pola <i>Strategy Pattern</i> lewat <i>CombatService</i>. Hal ini merampingkan <i>BattleController</i> sebesar 50%!</p>
*   **JRPG Story Mode (Visual Novel Overlay)**: <p align="justify">Setiap awal wave menampilkan percakapan interaktif berbalut glassmorphism yang menceritakan jenis error yang menghalangi commit pemain. Karakter yang berbicara disorot (<i>highlight</i>) secara dinamis.</p>
*   **Aturan Anti-Skip Progres**: <p align="justify">Tombol <i>Skip</i> dinonaktifkan jika pemain baru pertama kali mencoba Wave tersebut. Tombol <i>Skip >>></i> hanya aktif saat pemain mengulang Wave sebelumnya untuk mengumpulkan emas (<i>grinding</i>).</p>
*   **Battle Turn Idle Timer**: <p align="justify">Pemain diberikan batas berpikir selama 5 detik pada gilirannya. Jika tidak melakukan tindakan, sistem otomatis mengeksekusi Basic Attack untuk melindungi alur game.</p>
*   **Shop Purchase Lock & Deskripsi Detail**: <p align="justify">Fitur dinamis toko yang mengunci tombol pembelian senjata/armor dengan tulisan disabled <b>"PURCHASED"</b> jika sudah dibeli, serta deskripsi detail efek penyembuhan/peningkatan atribut di bawah nama item.</p>
*   **Database SQLite Lokal**: <p align="justify">Penyimpanan progres <i>save-game</i> dinamis yang menyimpan data wave tertinggi, emas, poin peningkatan, dan level Hero secara real-time.</p>
*   **Sistem Efek Suara & Musik Interaktif**: <p align="justify">Pengalaman bermain game yang semakin imersif dengan hadirnya integrasi efek audio dinamis (<i>sound effects</i>) pada setiap klik tombol, lantunan suara serangan fisik/sihir, suara kesalahan buzzer saat error, hingga audio selebrasi dramatis untuk merayakan kemenangan (<i>Victory</i>) atau kekalahan tragis (<i>Game Over</i>) melalui pustaka asinkron <i>JavaFX Media</i>.</p>

---

### 📈 Standar Pesan Commit (Conventional Commits)
Repository proyek ini dikelola secara rapi menggunakan standar Conventional Commits berikut:

| Type | Fungsi | Contoh Commit |
| :--- | :--- | :--- |
| **feat** | Fitur baru | `feat: tambahkan koneksi database` |
| **fix** | Perbaikan bug / error | `fix: ubah GetConnection menjadi connect` |
| **docs** | Dokumentasi | `docs: menambahkan readme` |
| **style** | Pemformatan / Tampilan (CSS) | `style: sesuaikan ukuran inventory scene menjadi 1024x680` |
| **refactor** | Restrukturisasi kode tanpa ubah fitur | `refactor: ubah kelas Hero menjadi abstract class dan perketat enkapsulasi` |
| **test** | Pengujian unit / integrasi | `test: perbarui unit test yang rusak untuk pengujian model Hero` |
| **chore** | Pemeliharaan / Konfigurasi | `chore: tambahkan dependensi sqlite jbdc` |

---

### 🚀 Cara Menjalankan Aplikasi
#### Prasyarat Sistem
*   Java JDK 17 atau versi lebih baru.
*   Konektivitas lokal untuk inisialisasi SQLite database (otomatis terbuat).

#### Langkah Eksekusi
1.  Kloning repositori proyek ini:
    ```bash
    git clone https://github.com/username/the-last-commit.git
    cd the-last-commit
    ```
2.  Jalankan aplikasi menggunakan Gradle Wrapper bawaan:
    *   **Di Windows**:
        ```cmd
        .\gradlew.bat run
        ```
    *   **Di macOS / Linux**:
        ```bash
        chmod +x gradlew
        ./gradlew run
        ```
3.  Lakukan pembersihan dan kompilasi ulang jika diperlukan:
    ```bash
    ./gradlew clean compileJava
    ```
