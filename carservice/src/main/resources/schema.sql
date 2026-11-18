-- Schema untuk CarService (Supabase PostgreSQL)
-- Jalankan di SQL editor Supabase atau via psql

-- Tabel Kategori (dipakai sebagai foreign key di Produk_Jasa)
CREATE TABLE IF NOT EXISTS Kategori (
    id_kategori SERIAL PRIMARY KEY,
    nama_kategori VARCHAR(255) NOT NULL UNIQUE
);

-- Tabel Customer
CREATE TABLE IF NOT EXISTS Customer (
    id_customer VARCHAR(50) PRIMARY KEY,
    nama_lengkap VARCHAR(255) NOT NULL,
    no_hp_whatsapp VARCHAR(50),
    alamat TEXT
);

-- Tabel Produk_Jasa
CREATE TABLE IF NOT EXISTS Produk_Jasa (
    kode VARCHAR(20) PRIMARY KEY,
    id_kategori INTEGER NOT NULL REFERENCES Kategori(id_kategori) ON DELETE RESTRICT,
    nama_produk_jasa VARCHAR(255) NOT NULL,
    merek VARCHAR(255),
    harga INTEGER NOT NULL,
    stok INTEGER,
    jenis VARCHAR(10) CHECK (jenis IN ('Produk', 'Jasa'))
);

-- Index untuk pencarian
CREATE INDEX IF NOT EXISTS idx_kategori_nama ON Kategori(nama_kategori);
CREATE INDEX IF NOT EXISTS idx_customer_nama ON Customer(nama_lengkap);
CREATE INDEX IF NOT EXISTS idx_customer_hp ON Customer(no_hp_whatsapp);
CREATE INDEX IF NOT EXISTS idx_produk_jasa_kode ON Produk_Jasa(kode);
CREATE INDEX IF NOT EXISTS idx_produk_jasa_kategori ON Produk_Jasa(id_kategori);
CREATE INDEX IF NOT EXISTS idx_produk_jasa_nama ON Produk_Jasa(nama_produk_jasa);
CREATE INDEX IF NOT EXISTS idx_produk_jasa_merek ON Produk_Jasa(merek);
