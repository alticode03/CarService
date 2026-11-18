# Alur Menu CarService

Dokumen ini menjelaskan pembagian alur terminal setelah menu dipisahkan dari
[`Main.java`](src/main/java/com/carservice/Main.java). Class DAO tetap berada di
`koneksidbc` dan tetap bertanggung jawab menjalankan operasi database.

## Struktur

```text
com/carservice/
  Main.java
  menu/
    InputTerminal.java
    MenuCustomer.java
    MenuKategori.java
    MenuKoneksi.java
    MenuProdukJasa.java

koneksidbc/
  Customer.java
  Kategori.java
  KoneksiSupabase.java
  Produk_Jasa.java
```

## Alur keseluruhan

```text
main()
  └─ Main.run()
      ├─ 1: MenuCustomer.run() ─ MenuCustomer → DAO Customer
      ├─ 2: MenuKategori.run() ─ MenuKategori → DAO Kategori
      ├─ 3: MenuProdukJasa.run() ─ MenuProdukJasa → DAO Produk_Jasa
      ├─ 4: MenuKoneksi.testDatabaseConnection()
      ├─ 5: MenuKoneksi.testSupabaseAuth()
      └─ 0: keluar
```

Menu-menu tersebut menerima `Scanner` dan `PrintStream` dari `Main`, sehingga
input/output terminal konsisten dan menu utama dapat diuji tanpa koneksi
database.

## `Main.java`

### `main(String[] args)`

Membuat scanner untuk `System.in` dan memulai aplikasi dengan output ke
`System.out`.

### `run(Scanner input, PrintStream output)`

Menampilkan menu utama dan meneruskan pilihan ke class yang bertanggung jawab:

| Pilihan | Tujuan |
| --- | --- |
| `1` | `MenuCustomer.run()` |
| `2` | `MenuKategori.run()` |
| `3` | `MenuProdukJasa.run()` |
| `4` | `MenuKoneksi.testDatabaseConnection()` |
| `5` | `MenuKoneksi.testSupabaseAuth()` |
| `0` | Keluar |

Jika input habis, aplikasi berhenti. Pilihan selain angka yang tersedia
menampilkan pesan kesalahan.

## Menu pelanggan

[`MenuCustomer.java`](src/main/java/com/carservice/menu/MenuCustomer.java)
mengatur prompt pelanggan dan memanggil DAO `Customer`.

- `1`: tampilkan semua pelanggan melalui `tampilSemuaCustomer()`.
- `2`: cari ID melalui `cariCustomerById()`.
- `3`: cari nama atau nomor HP melalui `cariCustomer()`.
- `4`: minta ID, nama, nomor HP, dan alamat, lalu panggil
  `tambahCustomer()`.
- `5`: minta ID serta seluruh nilai baru, lalu panggil `ubahCustomer()`.
- `6`: minta ID, konfirmasi `YA`, lalu panggil `hapusCustomer()`.
- `0`: kembali ke menu utama.

DAO dibuat saat submenu dibuka dan ditutup ketika pengguna keluar atau input
berakhir. Input opsional dan pesan hasil operasi menggunakan `InputTerminal`.

## Menu kategori

[`MenuKategori.java`](src/main/java/com/carservice/menu/MenuKategori.java)
mengatur prompt kategori dan memanggil DAO `Kategori`.

- `1`: tampilkan kategori dengan `tampil()`.
- `2`: cari ID dengan `cariById()`.
- `3`: cari nama dengan `cariByNama()`.
- `4`: minta nama kategori dan panggil `tambah()`; ID diberikan otomatis oleh
  database.
- `5`: minta ID dan nama baru, lalu panggil `ubah()`.
- `6`: minta ID, konfirmasi `YA`, lalu panggil `hapus()`.
- `0`: kembali ke menu utama.

## Menu produk/jasa

[`MenuProdukJasa.java`](src/main/java/com/carservice/menu/MenuProdukJasa.java)
mengatur prompt produk/jasa dan memanggil DAO `Produk_Jasa`.

- `1`: tampilkan seluruh produk/jasa, termasuk nama kategori.
- `2`: cari berdasarkan kode.
- `3`: cari berdasarkan nama atau merek.
- `4`: filter berdasarkan jenis `Produk` atau `Jasa`.
- `5`: filter berdasarkan ID kategori.
- `6`: minta kode, kategori, nama, merek, harga, stok, dan jenis, lalu tambah.
- `7`: minta data baru untuk item berkode tertentu, lalu ubah.
- `8`: minta kode, konfirmasi `YA`, lalu hapus.
- `9`: minta jenis dan tampilkan kode berikutnya.
- `10`: minta kode dan jumlah positif, lalu kurangi stok.
- `0`: kembali ke menu utama.

Merek dan stok boleh kosong. Jenis hanya menerima `Produk` atau `Jasa`.
Kolom-kolom yang ditampilkan disimpan pada `PRODUCT_COLUMNS`.

## Helper input dan tampilan

[`InputTerminal.java`](src/main/java/com/carservice/menu/InputTerminal.java)
memusatkan perilaku terminal yang dipakai oleh beberapa menu:

- `readLine()` menampilkan prompt dan membaca satu baris; `null` berarti input
  sudah habis.
- `readRequiredText()` menolak teks kosong.
- `readInteger()` mengulang prompt sampai menerima angka bulat.
- `readOptionalInteger()` mengembalikan angka, `Optional.empty()` jika kosong,
  atau `null` jika input habis.
- `readJenis()` memvalidasi dan menormalkan jenis menjadi `Produk` atau `Jasa`.
- `confirmDelete()` hanya menyetujui penghapusan jika pengguna mengetik `YA`.
- `printRows()` menampilkan setiap kolom hasil query dan menutup `ResultSet`.
- `reportUpdate()` menampilkan status operasi tambah/ubah/hapus.
- `closeDao()` menutup `Statement` dan koneksi setelah submenu selesai.

## Tes koneksi

[`MenuKoneksi.java`](src/main/java/com/carservice/menu/MenuKoneksi.java)
menangani kedua tes koneksi agar `Main` tetap menjadi dispatcher:

- `testDatabaseConnection()` menjalankan `SELECT 1`, menampilkan jenis database,
  dan memeriksa tabel `kategori`, `customer`, serta `produk_jasa` pada schema
  `public`.
- `testSupabaseAuth()` meneruskan tes health Auth API ke
  `KoneksiSupabase.testKoneksi()`.

## Tanggung jawab DAO

`Customer.java`, `Kategori.java`, dan `Produk_Jasa.java` tidak menjadi menu.
Method di ketiga class itu tetap membentuk dan menjalankan query CRUD. Class
menu hanya mengumpulkan input pengguna dan meneruskan nilainya ke DAO.
`KoneksiSupabase.java` menyediakan koneksi JDBC serta tes health API.

## Menjalankan aplikasi

Dari folder proyek `carservice`:

```powershell
mvn exec:java "-Dexec.mainClass=com.carservice.Main"
```
