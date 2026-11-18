# CarService

Aplikasi Java (Maven) untuk mengelola data Customer, Kategori, dan Produk/Jasa
dengan database Supabase (PostgreSQL).

## Struktur

```
CarService/
└─carservice/
  ├─README.md
  ├─AlurMain.md
  ├─.env
  ├─pom.xml
  └─src/
    ├─main/
    │  ├─java/
    │  │  ├─com/carservice/Main.java
    │  │  └─com/carservice/menu/
    │  │     ├─InputTerminal.java
    │  │     ├─MenuCustomer.java
    │  │     ├─MenuKategori.java
    │  │     ├─MenuKoneksi.java
    │  │     └─MenuProdukJasa.java
    │  │  └─koneksidbc/
    │  │     ├─KoneksiSupabase.java
    │  │     ├─Customer.java
    │  │     ├─Kategori.java
    │  │     └─Produk_Jasa.java
    │  └─resources/
    └─test/java/
```

## Konfigurasi

Buat file `.env` di folder `carservice` (jangan di-commit):

```
SUPABASE_URL=https://<project-ref>.supabase.co
SUPABASE_KEY=<publishable-key>
SUPABASE_DB_URL=jdbc:postgresql://db.<project-ref>.supabase.co:5432/postgres?user=postgres&password=<password-db>&sslmode=require
```

`SUPABASE_URL` + `SUPABASE_KEY` dipakai untuk cek health REST (`testKoneksi()`),
`SUPABASE_DB_URL` dipakai untuk koneksi JDBC ke database.

## Build & Jalankan

Buka folder `carservice` di VS Code. Jalankan perintah berikut dari terminal
PowerShell yang berada di folder tersebut. Jika terminal masih berada di
folder induk `CarService`, masuk dulu ke folder proyek:

```
mvn -version
mvn compile
mvn exec:java "-Dexec.mainClass=com.carservice.Main"
```

Saat menu tampil, ketik angka lalu tekan Enter:

1. `1` membuka menu CRUD pelanggan.
2. `2` membuka menu CRUD kategori.
3. `3` membuka menu CRUD produk/jasa, termasuk pencarian, filter, pembuatan kode,
   dan pengurangan stok.
4. `4` menguji koneksi JDBC dan memeriksa tabel `kategori`, `customer`, dan
   `produk_jasa` di schema `public`.
5. `5` menguji health endpoint Supabase Auth menggunakan `SUPABASE_URL` dan
   `SUPABASE_KEY`.
6. `0` keluar dari aplikasi.

Setiap submenu menyediakan opsi baca, tambah, ubah, dan hapus. Penghapusan
memerlukan konfirmasi dengan mengetik `YA`. Kategori baru menggunakan ID
otomatis dari database. Untuk produk/jasa, kategori yang dipilih harus sudah ada.

Jika menu nomor 1 melaporkan tabel belum ada, jalankan
[`src/main/resources/schema.sql`](./src/main/resources/schema.sql)
di SQL Editor Supabase.

## Memasang Maven di Windows

Jika `mvn` belum dikenali, jalankan blok berikut di PowerShell. Ini mengunduh
Apache Maven 3.9.9 ke profil pengguna, lalu menambahkan Maven ke `PATH` pengguna:

```powershell
$version = "3.9.9"
$installRoot = Join-Path $env:LOCALAPPDATA "Programs\Apache\Maven"
$mavenHome = Join-Path $installRoot "apache-maven-$version"
$zip = Join-Path $env:TEMP "apache-maven-$version-bin.zip"

New-Item -ItemType Directory -Force -Path $installRoot | Out-Null
Invoke-WebRequest `
  -Uri "https://archive.apache.org/dist/maven/maven-3/$version/binaries/apache-maven-$version-bin.zip" `
  -OutFile $zip
Expand-Archive -Path $zip -DestinationPath $installRoot -Force

[Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenHome, "User")
$mavenBin = Join-Path $mavenHome "bin"
$userPath = [Environment]::GetEnvironmentVariable("Path", "User")
if (($userPath -split ";") -notcontains $mavenBin) {
    $newPath = if ([string]::IsNullOrWhiteSpace($userPath)) {
        $mavenBin
    } else {
        "$userPath;$mavenBin"
    }
    [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
}
```

Tutup semua jendela VS Code, buka kembali, lalu jalankan `mvn -version` dari
terminal untuk memastikan Maven dikenali. Setelah itu, perintah Maven dapat
dijalankan dari terminal proyek maupun oleh sesi assistant yang baru.

## Pengujian

```
mvn test
```

Tes integrasi Supabase bersifat opt-in dan hanya membaca koneksi serta daftar
tabel. Jalankan dengan:

```
mvn "-Dsupabase.integration=true" "-Dtest=SupabaseConnectionTest" test
```

Perintah `mvn` memerlukan Apache Maven terpasang dan tersedia di `PATH`.
Pastikan dengan `mvn -version`, lalu jalankan perintah Maven dari folder proyek
`carservice`.

## Perubahan

- `KoneksiSupabase.java`: tambah method `conn()` yang mengembalikan `java.sql.Connection`
  ke Postgres Supabase lewat `SUPABASE_DB_URL` (sebelumnya hanya HTTP client untuk cek health).
- `Customer.java`, `Kategori.java`, `Produk_Jasa.java`: konstruktor tidak lagi memakai class
  `KoneksiJDBC` yang tidak ada di repo; sekarang memakai `new KoneksiSupabase().conn().createStatement()`.
- `Customer.java`, `Produk_Jasa.java`: deteksi foreign key gagal diubah dari kode error MySQL
  (`1451`/`1452`) ke SQLState PostgreSQL (`23503`), karena `getErrorCode()` selalu 0 di Postgres.
- `Produk_Jasa.java`: operator `REGEXP` diganti `~` (sintaks regex PostgreSQL) pada `buatKodeBaru()`.
- `pom.xml`: tambah dependency `org.postgresql:postgresql:42.7.4` sebagai driver JDBC.
- Source dipindah dari `src/main/koneksidbc/` ke `src/main/java/koneksidbc/` agar ikut dikompilasi Maven.
- `README.md`: kredensial Supabase (URL, password DB, JWT anon key) dihapus dari dokumentasi.