# AniKatsu - Aplikasi Pencarian Anime

Aplikasi mobile berbasis **Android** yang dikembangkan menggunakan **Kotlin**, **Jetpack Compose**, **Material Design 3**, **Navigation Component**, dan **Arsitektur MVVM (Model-View-ViewModel)**. Aplikasi ini memungkinkan pengguna untuk mencari katalog anime, memfilter berdasarkan genre, serta melihat informasi detail anime secara dinamis dari **Tenrai REST API** (`https://api.tenrai.org/v1/`).

---

## 📱 Ringkasan Fitur & Spesifikasi

1. **Bahasa Pemrograman (Kotlin)**
   - **Data Class**: Memodelkan struktur JSON API secara bersih dan type-safe.
   - **Null Safety**: Menggunakan *safe call* (`?.`), *elvis operator* (`?:`), dan tipe nullable untuk menangani respon API secara aman.
   - **Lambda**: Digunakan pada event handler UI, state reducer, filter collection, dan navigasi.
   - **Collection**: Transformasi list dengan `filter`, `sortedBy`, `distinctBy`, `mapNotNull`, dan `joinToString`.
   - **Coroutines & Flow**: Asynchronous networking dengan `viewModelScope`, `withContext(Dispatchers.IO)`, `StateFlow`, dan debounce pencarian.

2. **User Interface (Jetpack Compose & Material 3)**
   - Full Deklaratif UI dengan Jetpack Compose.
   - **Custom Theme**: Palet warna bernuansa anime (*Sakura Pink*, *Midnight Violet*, *Sunset Gold*, dan dark background) yang mendukung Light & Dark theme.
   - **Custom Typography**: Menggunakan font modern **Poppins** (`Regular`, `Medium`, `SemiBold`, `Bold`, `ExtraBold`) yang dimuat dari `res/font/`.
   - **State-driven UI**: Tampilan layar sepenuhnya dikendalikan oleh perubahan state.

3. **Daftar & Grid Anime**
   - Menggunakan `LazyVerticalGrid` 2 kolom responsif.
   - Setiap kartu menampilkan:
     - Poster anime (dimuat dengan Coil)
     - **Judul anime**
     - **Tipe anime** (TV, Movie, OVA, dll.)
     - **Rating / Skor** anime dengan ikon bintang

4. **Fitur Pencarian & Filter**
   - **Pencarian judul**: Search bar dengan debounce 600ms untuk efisiensi request dan aksi keyboard *Search*.
   - **Filter Genre**: Pilihan horizontal chip (`Semua`, `Action`, `Comedy`, dll.) yang terhubung ke endpoint `/v1/genres/anime`.

5. **Navigasi (Maksimal 2 Screen)**
   - **Home Screen**: Menampilkan header aplikasi, search bar, filter genre, dan grid anime.
   - **Detail Screen**: Menampilkan banner & poster, judul, rating, episode, status penayangan, daftar genre, informasi studio/musim/durasi, dan sinopsis lengkap.
   - Navigasi antar-layar menggunakan Jetpack Compose Navigation via argumen `mal_id`.

6. **Icon Aplikasi Kustom**
   - Icon default Android telah diganti dengan karakter chibi anime pada seluruh densitas `mipmap` (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi) serta adaptive icon Android 8.0+.

---

## 🏛️ Arsitektur Aplikasi (MVVM)

Aplikasi menerapkan pola arsitektur **MVVM (Model-View-ViewModel)** dengan pemisahan tanggung jawab yang jelas:

```
┌────────────────────────────────────────────────────────┐
│                   UI / Composable                      │
│      (HomeScreen, DetailScreen, AnimeCard, dll.)       │
└──────────────────────────┬─────────────────────────────┘
                           │ Mengamati State & Kirim Event
                           ▼
┌────────────────────────────────────────────────────────┐
│                      ViewModel                         │
│           (HomeViewModel, DetailViewModel)             │
│        - Mengelola UI State (StateFlow)                │
│        - Menjalankan Coroutines (viewModelScope)       │
└──────────────────────────┬─────────────────────────────┘
                           │ Memanggil fungsi suspend
                           ▼
┌────────────────────────────────────────────────────────┐
│                     Repository                         │
│                  (AnimeRepository)                     │
│        - Single Source of Truth bagi ViewModel         │
│        - Menjalankan operasi pada Dispatchers.IO       │
│        - Mengembalikan Result<T>                       │
└──────────────────────────┬─────────────────────────────┘
                           │ Request HTTP via Retrofit
                           ▼
┌────────────────────────────────────────────────────────┐
│                    Data & Remote                       │
│     - Model / Data Class (Anime, Genre, Pagination)    │
│     - TenraiApiService (Retrofit Interface)            │
│     - ApiClient (OkHttpClient + Base URL Tenrai)       │
└────────────────────────────────────────────────────────┘
```

### Penjelasan Komponen MVVM:

1. **Model / Data Class** (`data/model/Anime.kt`):
   - Merepresentasikan struktur data dari API.
   - Menggunakan anotasi `@SerializedName` dari Gson.
   - Dilengkapi *helper properties* seperti `imageUrl`, `scoreText`, dan `genreNames`.

2. **Repository** (`data/repository/AnimeRepository.kt`):
   - Mengisolasi sumber data dari ViewModel.
   - Fungsi `searchAnime(query, genreId)` mengambil hasil pencarian atau daftar anime populer.
   - Fungsi `getAnimeDetail(id)` mengambil detail spesifik berdasarkan `mal_id`.
   - Fungsi `getGenres()` mengambil daftar genre anime.

3. **ViewModel** (`ui/home/HomeViewModel.kt` & `ui/detail/DetailViewModel.kt`):
   - Bertanggung jawab memegang dan memodifikasi `uiState` (`StateFlow`).
   - Composable tidak pernah memanggil API secara langsung. ViewModel menangani error, loading, dan parsing pesan error pengguna.

4. **UI / Composable** (`ui/home/HomeScreen.kt` & `ui/detail/DetailScreen.kt`):
   - Stateless & state-driven UI.
   - Hanya menerima data `uiState` dan mengirim event pengguna (mis. `onQueryChange`, `onGenreSelected`, `onAnimeClick`).

---

## 🌐 Penggunaan API (Tenrai REST API)

Sesuai ketentuan, aplikasi menggunakan **Tenrai API** (REST API MyAnimeList yang modern dan cepat):
- **Base URL**: `https://api.tenrai.org/v1/`

### Endpoint yang Digunakan:

1. **Pencarian Anime & Populer**:
   - `GET /anime?q={query}&genres={id}&sfw=true`
   - Digunakan di `HomeScreen` saat pengguna mengetik judul atau memilih chip genre.
   - Jika query kosong dan tanpa filter, aplikasi memuat anime terpopuler (`order_by=popularity&sort=asc`).

2. **Detail Anime Berdasarkan ID**:
   - `GET /anime/{id}`
   - Contoh: `https://api.tenrai.org/v1/anime/1`
   - Mengambil informasi lengkap anime (judul, rating, episode, status, sinopsis, genre, studio).

3. **Daftar Genre Anime**:
   - `GET /genres/anime`
   - Digunakan untuk mengisi chip filter genre pada bagian atas daftar anime secara dinamis.

---

## 🗂️ Struktur File Proyek

```
app/src/main/
├── AndroidManifest.xml
├── java/com/pemmob/responsikotlinshiftg/
│   ├── MainActivity.kt
│   ├── data/
│   │   ├── model/
│   │   │   └── Anime.kt                 <-- Model data class & response
│   │   ├── remote/
│   │   │   ├── ApiClient.kt             <-- Retrofit instance & OkHttp
│   │   │   └── TenraiApiService.kt      <-- Retrofit interface endpoint
│   │   └── repository/
│   │       └── AnimeRepository.kt       <-- Single source of truth data
│   ├── ui/
│   │   ├── components/
│   │   │   ├── AnimeCard.kt             <-- Komponen item LazyVerticalGrid
│   │   │   └── StateViews.kt            <-- Loading, Error, & Empty states
│   │   ├── detail/
│   │   │   ├── DetailScreen.kt          <-- Layar detail anime
│   │   │   └── DetailViewModel.kt       <-- ViewModel detail anime
│   │   ├── home/
│   │   │   ├── HomeScreen.kt            <-- Layar utama + pencarian & filter
│   │   │   └── HomeViewModel.kt         <-- ViewModel pencarian & genre
│   │   ├── navigation/
│   │   │   └── AppNavigation.kt         <-- Navigation Host (2 screen)
│   │   └── theme/
│   │       ├── Color.kt                 <-- Custom color palette
│   │       ├── Theme.kt                 <-- Custom Material3 theme
│   │       └── Type.kt                  <-- Custom typography (Poppins)
│   └── util/
│       └── ErrorMapper.kt               <-- Konversi error ke bahasa Indonesia
└── res/
    ├── font/                            <-- Poppins font files
    ├── mipmap-*/                        <-- Custom app icon (Anime Character)
    └── values/
        └── strings.xml                  <-- App name "AniKatsu"
```

---

## 🚀 Cara Menjalankan Proyek

1. **Buka Proyek**: Buka folder `ResponsiKotlinShiftG` di Android Studio (Ladybug / Koala / versi terbaru).
2. **Sync Gradle**: Tunggu proses Gradle Sync selesai secara otomatis.
3. **Kompilasi**:
   ```bash
   ./gradlew assembleDebug
   ```
4. **Jalankan Aplikasi**: Jalankan pada emulator Android atau perangkat fisik dengan koneksi internet aktif.

---

## 📹 Rencana Rekaman Video Penjelasan (Panduan 8 Menit+)

Untuk memenuhi ketentuan pengumpulan video (1-2 menit demo + 8 menit+ penjelasan kode mendetail):
1. **00:00 - 02:00 (Demonstrasi Aplikasi)**:
   - Tunjukkan icon aplikasi custom di home screen launcher.
   - Demo pencarian anime di Home Screen (mis. ketik "Naruto" atau "Frieren").
   - Demo filter genre (mis. klik chip "Action", "Romance", "Semua").
   - Klik salah satu anime untuk masuk ke Detail Screen.
   - Tunjukkan informasi detail: Judul, rating, status, episode, genre, dan sinopsis.
   - Tekan tombol back untuk kembali ke Home Screen.
2. **02:00 - 04:00 (Penjelasan Arsitektur MVVM & Fitur Kotlin)**:
   - Buka `Anime.kt`: Jelaskan penggunaan `data class`, anotasi `@SerializedName`, dan *null safety* (`?`, `?:`).
   - Buka `TenraiApiService.kt` & `ApiClient.kt`: Jelaskan Retrofit suspend function dan Base URL `https://api.tenrai.org/v1/`.
   - Buka `AnimeRepository.kt`: Jelaskan bagaimana Coroutines (`Dispatchers.IO`), `runCatching`, serta fungsi collection (`distinctBy`, `sortedBy`) digunakan.
3. **04:00 - 06:30 (Penjelasan ViewModel & State Management)**:
   - Buka `HomeViewModel.kt` & `HomeUiState`: Jelaskan 5 state utama (`query`, `animeList`, `isLoading`, `errorMessage`, `selectedGenre`).
   - Jelaskan mekanisme `StateFlow`, `update`, coroutine debounce saat pengetikan, serta penanganan error.
   - Buka `DetailViewModel.kt`: Jelaskan penggunaan `SavedStateHandle` untuk menerima `animeId` dari Navigation.
4. **06:30 - 08:30+ (Penjelasan Jetpack Compose UI, Material 3, Theme, & Navigation)**:
   - Buka `Theme.kt`, `Color.kt`, dan `Type.kt`: Jelaskan kustomisasi Material 3 dan penggunaan font Poppins.
   - Buka `HomeScreen.kt`: Jelaskan `collectAsStateWithLifecycle()`, recomposition ketika state berubah, `OutlinedTextField`, `LazyRow` filter, dan `LazyVerticalGrid`.
   - Buka `DetailScreen.kt`: Jelaskan penyusunan komponen tampilan detail, AsyncImage Coil, dan penanganan loading/error.
   - Buka `AppNavigation.kt`: Jelaskan arsitektur navigasi maksimal 2 screen.
