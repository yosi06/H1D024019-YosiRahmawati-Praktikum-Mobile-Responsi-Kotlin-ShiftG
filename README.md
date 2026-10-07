# AniKatsu 🎌

Aplikasi katalog dan pencarian anime modern berbasis Android yang dibangun dengan **Kotlin**, **Jetpack Compose**, **Material Design 3**, dan **Arsitektur MVVM (Model-View-ViewModel)**.

---

## 📋 Daftar Isi
1. [Nama Aplikasi](#-nama-aplikasi)
2. [Deskripsi Singkat Aplikasi](#-deskripsi-singkat-aplikasi)
3. [Screenshot & GIF Aplikasi](#-screenshot--gif-aplikasi)
4. [Penjelasan Struktur MVVM](#-penjelasan-struktur-mvvm)
5. [Penjelasan Penggunaan API](#-penjelasan-penggunaan-api)
6. [Fitur Unggulan](#-fitur-unggulan)
7. [Struktur Direktori Proyek](#-struktur-direktori-proyek)
8. [Cara Menjalankan Aplikasi](#-cara-menjalankan-aplikasi)

---

## 🏷️ Nama Aplikasi

**AniKatsu** *(Anime Catalog & Discovery App)*

- **Package Name**: `com.pemmob.responsikotlinshiftg`
- **Platform**: Android (Min SDK 24, Target SDK 35, Compile SDK 35 / 37.1)
- **Bahasa**: Kotlin (JVM Toolchain 17 / 21)
- **UI Framework**: Jetpack Compose + Material Design 3

---

## 📝 Deskripsi Singkat Aplikasi

**AniKatsu** adalah aplikasi penjelajah anime yang dirancang untuk memberikan pengalaman pengguna yang cepat, responsif, dan elegan. Aplikasi ini memungkinkan pengguna untuk:
- Menjelajahi katalog anime populer secara dinamis dalam bentuk *grid responsif* 2 kolom.
- Mencari anime berdasarkan judul dengan fitur *real-time search debounce* (500 ms) untuk menghemat konsumsi kuota data dan beban request ke server.
- Memfilter anime berdasarkan genre pilihan (seperti Action, Adventure, Fantasy, Comedy, Romance, dll.) menggunakan *Filter Chips* horizontal.
- Melihat rincian mendalam pada setiap anime: poster resolusi tinggi dengan efek *sinematik blur header*, skor rating bintang, jumlah episode, status penayangan, genre tagar (`FlowRow`), informasi musim dan studio produksi, hingga sinopsis lengkap.
- Mengakses aplikasi dengan antarmuka bertema gelap (*Dark Mode*) yang nyaman di mata dengan aksen warna *Coral Red*, *Teal*, dan *Sunset Gold*, serta tipografi modern dari keluarga font **Poppins**.

---

## 📸 Screenshot & GIF Aplikasi

| Home Screen | Detail Screen | Demo Interaksi (GIF) |
| :---: | :---: | :---: |
| <img src="docs/screenshots/home_screen.png" width="240" alt="Home Screen AniKatsu" /> | <img src="docs/screenshots/detail_screen.png" width="240" alt="Detail Screen AniKatsu" /> | <img src="docs/screenshots/demo.gif" width="240" alt="Demo Animasi AniKatsu" /> |
| *Katalog anime, search bar, & filter genre* | *Banner sinematik, info rating, genre, & sinopsis* | *Animasi pencarian, filter, dan transisi layar* |

> **Catatan Pengujian:**  
> File gambar dapat ditempatkan pada folder [`docs/screenshots/`](docs/screenshots/) dengan nama `home_screen.png`, `detail_screen.png`, dan `demo.gif`.

---

## 🏛️ Penjelasan Struktur MVVM

Aplikasi ini menerapkan pola arsitektur **MVVM (Model-View-ViewModel)** dengan prinsip pemisahan tanggung jawab (*Separation of Concerns*) dan *Unidirectional Data Flow (UDF)*:

```
┌─────────────────────────────────────────────────────────────────┐
│                    VIEW (Jetpack Compose UI)                    │
│   HomeScreen, DetailScreen, AnimeCard, StateViews, Theme, Nav   │
└───────────────────────────────┬─────────────────────────────────┘
                                │ 1. Mengamati UiState (StateFlow)
                                │ 2. Mengirim User Event (Lambda)
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                           VIEWMODEL                             │
│               HomeViewModel & DetailViewModel                   │
│   - Memegang Single Source of Truth via MutableStateFlow        │
│   - Menjalankan Coroutines pada viewModelScope                  │
│   - Menerapkan Debounce Search (delay 500ms)                    │
│   - Menangani SavedStateHandle untuk navigasi berargumen        │
└───────────────────────────────┬─────────────────────────────────┘
                                │ Memanggil suspend function
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                           REPOSITORY                            │
│                        AnimeRepository                          │
│   - Mengabstraksi sumber data (Single Source of Truth)          │
│   - Memindahkan thread ke Dispatchers.IO                        │
│   - Menangkap error dengan runCatching -> Result<T>             │
└───────────────────────────────┬─────────────────────────────────┘
                                │ Permintaan HTTP Network
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                   DATA LAYER (Remote & Model)                   │
│   - TenraiApiService (Retrofit REST Interface)                  │
│   - ApiClient (OkHttpClient + Logging Interceptor + Timeout)    │
│   - Model/DTO (Anime, AnimeImages, Genre, Studio, Response)     │
└─────────────────────────────────────────────────────────────────┘
```

### 1. Model (Data Layer)
- Terletak di paket `data.model` ([`Anime.kt`](app/src/main/java/com/pemmob/responsikotlinshiftg/data/model/Anime.kt)).
- Menggunakan **Kotlin Data Class** untuk memodelkan struktur payload JSON dari API secara *type-safe*.
- Menggunakan anotasi `@SerializedName` dari Gson untuk memetakan nama field JSON ke variabel Kotlin.
- Menerapkan **Null Safety** secara ketat (tipe nullable `?` dan default value `null`) agar aplikasi tahan terhadap data API yang tidak lengkap.
- Menyediakan *computed properties* pembantu seperti `imageUrl` (fallback bertingkat WebP/JPG) dan `scoreText` (format dua desimal).

### 2. View (UI Layer)
- Terletak di paket `ui.home`, `ui.detail`, `ui.components`, `ui.theme`, dan `ui.navigation`.
- Dibangun 100% secara deklaratif menggunakan **Jetpack Compose** dan **Material Design 3**.
- Bersifat **Stateless**: Composable hanya menerima `UiState` dari ViewModel dan meneruskan event interaksi pengguna ke atas (*State Hoisting*).
- Menggunakan `collectAsStateWithLifecycle()` dari `androidx.lifecycle.runtime.compose` agar konsumsi state otomatis berhenti saat activity di latar belakang.
- Penanganan status UI terisolasi rapi: `LoadingView` saat memuat data, `ErrorView` saat terjadi kegagalan jaringan beserta tombol *retry*, dan `EmptyView` jika pencarian tidak menemukan hasil.

### 3. ViewModel (Business Logic & State Management)
- Terletak di [`HomeViewModel.kt`](app/src/main/java/com/pemmob/responsikotlinshiftg/ui/home/HomeViewModel.kt) dan [`DetailViewModel.kt`](app/src/main/java/com/pemmob/responsikotlinshiftg/ui/detail/DetailViewModel.kt).
- Menyimpan kondisi layar di dalam satu `HomeUiState` / `DetailUiState`.
- Menerapkan enkapsulasi: `_uiState` berjenis `MutableStateFlow` bersifat privat, sedangkan antarmuka luar hanya membaca `uiState` bertipe `StateFlow` *read-only*.
- Menjalankan operasi asinkron dalam cakupan `viewModelScope` sehingga seluruh proses otomatis dibatalkan jika ViewModel dihancurkan (*memory-leak proof*).
- Pada `HomeViewModel`, pencarian dilengkapi mekanisme pembatalan job (`searchJob?.cancel()`) dan jeda `delay(500)` milidetik (*debounce*).
- Pada `DetailViewModel`, parameter `animeId` diambil secara aman menggunakan `SavedStateHandle`.

### 4. Repository (Data Abstraction Layer)
- Terletak di [`AnimeRepository.kt`](app/src/main/java/com/pemmob/responsikotlinshiftg/data/repository/AnimeRepository.kt).
- Berfungsi sebagai jembatan tunggal antara ViewModel dan sumber data jaringan Retrofit.
- Mengeksekusi request jaringan di thread background menggunakan `withContext(Dispatchers.IO)`.
- Membungkus respon dalam konstruksi `runCatching` yang mengembalikan tipe Kotlin `Result<T>` (`onSuccess` / `onFailure`).

---

## 🌐 Penjelasan Penggunaan API

Aplikasi AniKatsu memanfaatkan REST API publik modern dari **Tenrai API**:

- **Provider**: [Tenrai REST API](https://api.tenrai.org/)
- **Base URL**: `https://api.tenrai.org/v1/`
- **Format Respons**: JSON

### 1. Endpoint yang Digunakan

| No | Endpoint HTTP | Fungsi dalam Aplikasi | Letak Penggunaan |
| :---: | :--- | :--- | :--- |
| **1** | `GET /v1/anime` | Mengambil daftar katalog anime terpopuler, mendukung pencarian kata kunci (`?q={keyword}`) dan filter genre (`?genres={id}`). | `HomeViewModel.loadAnime()` |
| **2** | `GET /v1/anime/{id}` | Mengambil detail lengkap satu anime berdasarkan ID unik (judul alternatif, episode, rating, durasi, musim, studio, sinopsis). | `DetailViewModel.loadDetail()` |
| **3** | `GET /v1/genres/anime` | Mengambil daftar seluruh kategori genre anime untuk mengisi pilihan filter chip secara dinamis. | `HomeViewModel.loadGenres()` |

### 2. Implementasi Klien Jaringan (Retrofit & OkHttp)
Konfigurasi klien jaringan terletak pada [`ApiClient.kt`](app/src/main/java/com/pemmob/responsikotlinshiftg/data/remote/ApiClient.kt):
- **Singleton Pattern**: Menggunakan `object ApiClient` agar instance Retrofit hanya dibuat satu kali sepanjang aplikasi aktif.
- **HttpLoggingInterceptor**: Dikonfigurasi dengan level `BODY` untuk mencatat request URL, header, serta payload body pada jendela Logcat selama proses *development*.
- **Timeouts**: Mengonfigurasi `connectTimeout`, `readTimeout`, dan `writeTimeout` masing-masing 30 detik untuk mengantisipasi jaringan internet yang lambat.
- **GsonConverterFactory**: Melakukan deserialisasi otomatis dari dokumen JSON ke objek data class Kotlin.

### 3. Penanganan Galat Jaringan (Error Mapping)
Terletak pada [`ErrorMapper.kt`](app/src/main/java/com/pemmob/responsikotlinshiftg/util/ErrorMapper.kt):
- Menggunakan *Extension Function* `Throwable.toUserMessage()` untuk memetakan galat jaringan menjadi pesan ramah dalam Bahasa Indonesia:
  - `IOException` -> Gangguan koneksi internet pengguna.
  - HTTP 404 -> Anime tidak ditemukan.
  - HTTP 429 -> Batas kuota request terlampaui (*Rate Limit*).
  - HTTP 500-599 -> Server API sedang mengalami kendala.

---

## ✨ Fitur Unggulan

1. **Kotlin Features Modern**:
   - *Data classes* ringkas dan *immutable*.
   - *Null safety* dengan safe call operator (`?.`) dan Elvis operator (`?:`).
   - *Extension functions* (`Throwable.toUserMessage()`).
   - *Coroutines* & *Flow* untuk pemrograman asinkron non-blocking.
2. **Desain Visual Material 3**:
   - Palet warna custom gelap (*Midnight Slate*, *Coral Primary*, *Sunset Gold*).
   - Tipografi elegan menggunakan font **Poppins** (`Regular`, `Medium`, `SemiBold`, `Bold`).
   - Header detail sinematik dengan poster blur gradien dan kartu info melayang (*elevated cards*).
   - Badge skor rating bernuansa emas dan tag genre responsif menggunakan `FlowRow`.
3. **Pemuatan Gambar Asinkron**:
   - Menggunakan pustaka **Coil Compose** (`AsyncImage`) dengan *caching* memori otomatis dan penyesuaian skala *ContentScale.Crop*.
4. **Navigasi Maksimal 2 Layar**:
   - Layar Utama (`HomeScreen`) menuju Layar Rincian (`DetailScreen`) menggunakan Jetpack Navigation Compose dengan argumen `NavType.IntType`.
5. **Icon Aplikasi Kustom**:
   - Icon aplikasi unik karakter anime pada seluruh resolusi `res/mipmap-*/` dan *adaptive icon* Android 8.0+.

---

## 📂 Struktur Direktori Proyek

```
app/src/main/
├── AndroidManifest.xml                        # Konfigurasi aplikasi & izin INTERNET
├── java/com/pemmob/responsikotlinshiftg/
│   ├── MainActivity.kt                        # Entry point (enableEdgeToEdge & setContent)
│   ├── data/
│   │   ├── model/
│   │   │   └── Anime.kt                       # Data class model & response DTO
│   │   ├── remote/
│   │   │   ├── ApiClient.kt                   # Retrofit & OkHttpClient singleton
│   │   │   └── TenraiApiService.kt            # Definisi endpoint Retrofit
│   │   └── repository/
│   │       └── AnimeRepository.kt             # Repository pola data
│   ├── ui/
│   │   ├── components/
│   │   │   ├── AnimeCard.kt                   # Komponen kartu anime grid
│   │   │   └── StateViews.kt                  # Komponen Loading, Error, & Empty
│   │   ├── detail/
│   │   │   ├── DetailScreen.kt                # UI Layar Detail anime
│   │   │   └── DetailViewModel.kt             # ViewModel Layar Detail
│   │   ├── home/
│   │   │   ├── HomeScreen.kt                  # UI Layar Utama + Search & Filter
│   │   │   └── HomeViewModel.kt               # ViewModel Layar Utama
│   │   ├── navigation/
│   │   │   └── AppNavigation.kt               # NavHost & rute navigasi
│   │   └── theme/
│   │       ├── Color.kt                       # Palet warna Material 3
│   │       ├── Theme.kt                       # Theme provider AniKatsu
│   │       └── Type.kt                        # Konfigurasi Typography Poppins
│   └── util/
│       └── ErrorMapper.kt                     # Konversi error Throwable ke teks
└── res/
    ├── font/                                  # File font Poppins (Regular, Medium, SemiBold, Bold)
    ├── mipmap-*/                              # Icon launcher aplikasi kustom
    └── values/
        └── strings.xml                        # String resource (app_name = "AniKatsu")
```

---

## 🚀 Cara Menjalankan Aplikasi

1. **Clone atau Buka Proyek**: Buka folder repositori ini di **Android Studio** (Koala / Ladybug / versi terbaru).
2. **Koneksi Internet**: Pastikan komputer terhubung ke internet untuk mendownload dependensi Gradle dan melakukan panggilan API.
3. **Gradle Sync**: Tunggu proses *Gradle Project Sync* selesai.
4. **Build Proyek**:
   ```bash
   ./gradlew assembleDebug
   ```
5. **Jalankan Aplikasi**: Pilih emulator Android (API level 24 ke atas) atau perangkat fisik via USB Debugging, lalu klik tombol **Run** (`Shift + F10`).
