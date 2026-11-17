package koneksidbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Produk_Jasa {

    public Statement st;

    // Query dasar: join ke Kategori supaya nama kategori ikut tampil
    private static final String SELECT_JOIN =
            "SELECT p.kode, p.id_kategori, k.nama_kategori, p.nama_produk_jasa, "
          + "p.merek, p.harga, p.stok, p.jenis "
          + "FROM Produk_Jasa p JOIN Kategori k ON p.id_kategori = k.id_kategori ";

    // Constructor: menyiapkan koneksi dan menyimpan statement utama ke st
    public Produk_Jasa() {
        KoneksiJDBC kj = new KoneksiJDBC();
        kj.config();
        this.st = kj.stm;
    }

    // Mengamankan tanda kutip tunggal agar query tidak error
    private String esc(String s) {
        return s == null ? "" : s.replace("'", "''");
    }

    // Teks kosong/null disimpan sebagai NULL
    private String txtOrNull(String s) {
        return (s == null || s.trim().isEmpty()) ? "NULL" : "'" + esc(s.trim()) + "'";
    }

    // ===================== CREATE =====================
    public boolean tambahProdukJasa(String kode, int idKategori, String namaProdukJasa,
                                    String merek, int harga, Integer stok, String jenis) {
        try {
            if (!jenis.equals("Produk") && !jenis.equals("Jasa")) {
                System.out.println("Gagal tambah: jenis harus 'Produk' atau 'Jasa'.");
                return false;
            }
            String stokSql = (stok == null) ? "NULL" : String.valueOf(stok);

            String sql = "INSERT INTO Produk_Jasa "
                    + "(kode, id_kategori, nama_produk_jasa, merek, harga, stok, jenis) "
                    + "VALUES ('" + esc(kode) + "', " + idKategori + ", '"
                    + esc(namaProdukJasa) + "', " + txtOrNull(merek) + ", "
                    + harga + ", " + stokSql + ", '" + jenis + "')";
            return st.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) { // foreign key gagal
                System.out.println("Gagal tambah: id_kategori " + idKategori + " tidak ada di tabel Kategori.");
            } else {
                System.out.println("Gagal tambah produk/jasa: " + e.getMessage());
            }
            return false;
        }
    }

    // Membuat kode otomatis: PR001, PR002, ... (Produk) atau JS001, JS002, ... (Jasa)
    public String buatKodeBaru(String jenis) {
        String prefix = jenis.equals("Produk") ? "PR" : "JS";
        try {
            ResultSet rs = st.executeQuery("SELECT MAX(kode) FROM Produk_Jasa "
                    + "WHERE kode REGEXP '^" + prefix + "[0-9]{3}$'");
            int nomor = 1;
            if (rs.next() && rs.getString(1) != null) {
                nomor = Integer.parseInt(rs.getString(1).substring(2)) + 1;
            }
            rs.close();
            return String.format("%s%03d", prefix, nomor);
        } catch (SQLException e) {
            System.out.println("Gagal membuat kode: " + e.getMessage());
            return null;
        }
    }

    // ===================== READ =====================
    public ResultSet tampilSemua() {
        try {
            return st.executeQuery(SELECT_JOIN + "ORDER BY p.kode");
        } catch (SQLException e) {
            System.out.println("Gagal tampil data: " + e.getMessage());
            return null;
        }
    }

    public ResultSet cariByKode(String kode) {
        try {
            return st.executeQuery(SELECT_JOIN + "WHERE p.kode = '" + esc(kode) + "'");
        } catch (SQLException e) {
            System.out.println("Gagal cari data: " + e.getMessage());
            return null;
        }
    }

    // Cari berdasarkan nama atau merek (untuk kolom search di GUI)
    public ResultSet cari(String keyword) {
        try {
            String k = esc(keyword);
            return st.executeQuery(SELECT_JOIN + "WHERE p.nama_produk_jasa LIKE '%" + k
                    + "%' OR p.merek LIKE '%" + k + "%' ORDER BY p.nama_produk_jasa");
        } catch (SQLException e) {
            System.out.println("Gagal cari data: " + e.getMessage());
            return null;
        }
    }

    // Filter berdasarkan jenis: "Produk" atau "Jasa"
    public ResultSet tampilByJenis(String jenis) {
        try {
            return st.executeQuery(SELECT_JOIN + "WHERE p.jenis = '" + esc(jenis)
                    + "' ORDER BY p.kode");
        } catch (SQLException e) {
            System.out.println("Gagal filter jenis: " + e.getMessage());
            return null;
        }
    }

    // Filter berdasarkan kategori
    public ResultSet tampilByKategori(int idKategori) {
        try {
            return st.executeQuery(SELECT_JOIN + "WHERE p.id_kategori = " + idKategori
                    + " ORDER BY p.kode");
        } catch (SQLException e) {
            System.out.println("Gagal filter kategori: " + e.getMessage());
            return null;
        }
    }

    // ===================== UPDATE =====================
    public boolean ubahProdukJasa(String kode, int idKategori, String namaProdukJasa,
                                  String merek, int harga, Integer stok, String jenis) {
        try {
            if (!jenis.equals("Produk") && !jenis.equals("Jasa")) {
                System.out.println("Gagal ubah: jenis harus 'Produk' atau 'Jasa'.");
                return false;
            }
            String stokSql = (stok == null) ? "NULL" : String.valueOf(stok);

            String sql = "UPDATE Produk_Jasa SET "
                    + "id_kategori = " + idKategori + ", "
                    + "nama_produk_jasa = '" + esc(namaProdukJasa) + "', "
                    + "merek = " + txtOrNull(merek) + ", "
                    + "harga = " + harga + ", "
                    + "stok = " + stokSql + ", "
                    + "jenis = '" + jenis + "' "
                    + "WHERE kode = '" + esc(kode) + "'";
            return st.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) {
                System.out.println("Gagal ubah: id_kategori " + idKategori + " tidak ada di tabel Kategori.");
            } else {
                System.out.println("Gagal ubah produk/jasa: " + e.getMessage());
            }
            return false;
        }
    }

    // Kurangi stok saat transaksi (hanya untuk Produk dan hanya jika stok cukup)
    public boolean kurangiStok(String kode, int jumlah) {
        try {
            String sql = "UPDATE Produk_Jasa SET stok = stok - " + jumlah
                    + " WHERE kode = '" + esc(kode) + "' AND jenis = 'Produk' AND stok >= " + jumlah;
            return st.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            System.out.println("Gagal kurangi stok: " + e.getMessage());
            return false;
        }
    }

    // ===================== DELETE =====================
    public boolean hapusProdukJasa(String kode) {
        try {
            return st.executeUpdate("DELETE FROM Produk_Jasa WHERE kode = '"
                    + esc(kode) + "'") > 0;
        } catch (SQLException e) {
            // Error 1451 = masih dipakai di Detail_Transaksi (ON DELETE RESTRICT)
            if (e.getErrorCode() == 1451) {
                System.out.println("Gagal hapus: " + kode + " sudah dipakai di transaksi.");
            } else {
                System.out.println("Gagal hapus produk/jasa: " + e.getMessage());
            }
            return false;
        }
    }

    // ===================== KATEGORI (pendukung, karena id_kategori adalah FK) =====================
    // Mengembalikan id_kategori baru (AUTO_INCREMENT), atau -1 jika gagal
    public int tambahKategori(String namaKategori) {
        try {
            st.executeUpdate("INSERT INTO Kategori (nama_kategori) VALUES ('"
                    + esc(namaKategori) + "')", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = st.getGeneratedKeys();
            int id = rs.next() ? rs.getInt(1) : -1;
            rs.close();
            return id;
        } catch (SQLException e) {
            System.out.println("Gagal tambah kategori: " + e.getMessage());
            return -1;
        }
    }

    public boolean hapusKategori(int idKategori) {
        try {
            return st.executeUpdate("DELETE FROM Kategori WHERE id_kategori = "
                    + idKategori) > 0;
        } catch (SQLException e) {
            System.out.println("Gagal hapus kategori: " + e.getMessage());
            return false;
        }
    }
}
