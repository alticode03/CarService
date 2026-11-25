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

-- =========================================
-- TABEL TRANSAKSI
-- =========================================

CREATE TABLE IF NOT EXISTS transaksi (
    id_transaksi VARCHAR(50) PRIMARY KEY,
    id_customer VARCHAR(50) NOT NULL
        REFERENCES customer(id_customer) ON DELETE RESTRICT,
    tanggal_transaksi TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_harga BIGINT NOT NULL CHECK (total_harga > 0)
);

-- =========================================
-- TABEL DETAIL TRANSAKSI
-- =========================================

CREATE TABLE IF NOT EXISTS detail_transaksi (
    id_detail BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_transaksi VARCHAR(50) NOT NULL
        REFERENCES transaksi(id_transaksi) ON DELETE RESTRICT,
    kode VARCHAR(20) NOT NULL
        REFERENCES produk_jasa(kode) ON DELETE RESTRICT,
    nama_item VARCHAR(255) NOT NULL,
    jenis_item VARCHAR(10) NOT NULL
        CHECK (jenis_item IN ('Produk', 'Jasa')),
    jumlah INTEGER NOT NULL CHECK (jumlah > 0),
    harga_satuan BIGINT NOT NULL CHECK (harga_satuan > 0),
    subtotal BIGINT NOT NULL CHECK (subtotal > 0),
    CHECK (subtotal = jumlah * harga_satuan)
);

-- =========================================
-- TABEL PEMBAYARAN
-- =========================================

CREATE TABLE IF NOT EXISTS pembayaran (
    id_pembayaran BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_transaksi VARCHAR(50) NOT NULL
        REFERENCES transaksi(id_transaksi) ON DELETE RESTRICT,
    metode_pembayaran VARCHAR(20) NOT NULL
        CHECK (metode_pembayaran IN ('Tunai', 'QRIS', 'Transfer')),
    jumlah_dibayar BIGINT NOT NULL CHECK (jumlah_dibayar > 0),
    uang_diterima BIGINT NOT NULL CHECK (uang_diterima > 0),
    uang_kembalian BIGINT NOT NULL DEFAULT 0
        CHECK (uang_kembalian >= 0),

    UNIQUE (id_transaksi, metode_pembayaran),

    CHECK (
        (metode_pembayaran = 'Tunai'
            AND uang_diterima >= jumlah_dibayar
            AND uang_kembalian = uang_diterima - jumlah_dibayar)
        OR
        (metode_pembayaran IN ('QRIS', 'Transfer')
            AND uang_diterima = jumlah_dibayar
            AND uang_kembalian = 0)
    )
);

-- =========================================
-- INDEX TRANSAKSI
-- =========================================

CREATE INDEX IF NOT EXISTS idx_transaksi_customer
    ON transaksi(id_customer);

CREATE INDEX IF NOT EXISTS idx_transaksi_tanggal
    ON transaksi(tanggal_transaksi);

CREATE INDEX IF NOT EXISTS idx_detail_transaksi_id
    ON detail_transaksi(id_transaksi);

CREATE INDEX IF NOT EXISTS idx_pembayaran_transaksi
    ON pembayaran(id_transaksi);
