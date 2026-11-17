package koneksidbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer {

    public Statement st;

    // Constructor: menyiapkan koneksi dan menyimpan statement utama ke st
    public Customer() {
        KoneksiJDBC kj = new KoneksiJDBC();
        kj.config();
        this.st = kj.stm;
    }

    // Mengamankan tanda kutip tunggal agar query tidak error
    private String esc(String s) {
        return s == null ? "" : s.replace("'", "''");
    }

    // ===================== CREATE =====================
    public boolean tambahCustomer(String idCustomer, String namaLengkap,
                                  String noHpWhatsapp, String alamat) {
        try {
            String alamatSql = (alamat == null || alamat.isEmpty())
                    ? "NULL" : "'" + esc(alamat) + "'";

            String sql = "INSERT INTO Customer "
                    + "(id_customer, nama_lengkap, no_hp_whatsapp, alamat) "
                    + "VALUES ('" + esc(idCustomer) + "', '" + esc(namaLengkap) + "', '"
                    + esc(noHpWhatsapp) + "', " + alamatSql + ")";
            return st.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            System.out.println("Gagal tambah customer: " + e.getMessage());
            return false;
        }
    }

    // ===================== READ =====================
    // Ambil semua customer
    public ResultSet tampilSemuaCustomer() {
        try {
            return st.executeQuery("SELECT * FROM Customer ORDER BY id_customer");
        } catch (SQLException e) {
            System.out.println("Gagal tampil customer: " + e.getMessage());
            return null;
        }
    }

    // Ambil satu customer berdasarkan id
    public ResultSet cariCustomerById(String idCustomer) {
        try {
            return st.executeQuery("SELECT * FROM Customer WHERE id_customer = '"
                    + esc(idCustomer) + "'");
        } catch (SQLException e) {
            System.out.println("Gagal cari customer: " + e.getMessage());
            return null;
        }
    }

    // Cari berdasarkan nama atau no HP (untuk kolom search di GUI)
    public ResultSet cariCustomer(String keyword) {
        try {
            String k = esc(keyword);
            return st.executeQuery("SELECT * FROM Customer WHERE nama_lengkap LIKE '%" + k
                    + "%' OR no_hp_whatsapp LIKE '%" + k + "%' ORDER BY nama_lengkap");
        } catch (SQLException e) {
            System.out.println("Gagal cari customer: " + e.getMessage());
            return null;
        }
    }

    // ===================== UPDATE =====================
    public boolean ubahCustomer(String idCustomer, String namaLengkap,
                                String noHpWhatsapp, String alamat) {
        try {
            String alamatSql = (alamat == null || alamat.isEmpty())
                    ? "NULL" : "'" + esc(alamat) + "'";

            String sql = "UPDATE Customer SET "
                    + "nama_lengkap = '" + esc(namaLengkap) + "', "
                    + "no_hp_whatsapp = '" + esc(noHpWhatsapp) + "', "
                    + "alamat = " + alamatSql + " "
                    + "WHERE id_customer = '" + esc(idCustomer) + "'";
            return st.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            System.out.println("Gagal ubah customer: " + e.getMessage());
            return false;
        }
    }

    // ===================== DELETE =====================
    public boolean hapusCustomer(String idCustomer) {
        try {
            return st.executeUpdate("DELETE FROM Customer WHERE id_customer = '"
                    + esc(idCustomer) + "'") > 0;
        } catch (SQLException e) {
            // Error 1451 = masih dipakai di tabel Transaksi (ON DELETE RESTRICT)
            if (e.getErrorCode() == 1451) {
                System.out.println("Gagal hapus: customer " + idCustomer
                        + " sudah punya transaksi.");
            } else {
                System.out.println("Gagal hapus customer: " + e.getMessage());
            }
            return false;
        }
    }
}