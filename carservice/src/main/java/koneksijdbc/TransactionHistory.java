
package koneksijdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionHistory {

    private Connection conn;
    public PreparedStatement st;

    public TransactionHistory() {
        try {
            this.conn = new KoneksiSupabase().conn();
        } catch (SQLException e) {
            throw new IllegalStateException(
                "Gagal koneksi ke Supabase: " + e.getMessage(), e
            );
        }
    }

    private void siapkanQuery(String sql) throws SQLException {
        if (st != null) {
            st.close();
        }
        st = conn.prepareStatement(sql);
    }

    // 1. Menampilkan seluruh transaksi
    public ResultSet tampilSemuaTransaksi() {
        String sql = """
            SELECT
                t.id_transaksi,
                c.nama_lengkap,
                t.tanggal_transaksi,
                t.total_harga
            FROM transaksi t
            JOIN customer c
                ON t.id_customer = c.id_customer
            ORDER BY t.tanggal_transaksi DESC
            """;

        try {
            siapkanQuery(sql);
            return st.executeQuery();
        } catch (SQLException e) {
            System.out.println("Gagal menampilkan transaksi: " + e.getMessage());
            return null;
        }
    }

    // 2. Mencari transaksi berdasarkan ID
    public ResultSet cariTransaksiById(String id) {
        String sql = """
            SELECT
                t.id_transaksi,
                c.nama_lengkap,
                t.tanggal_transaksi,
                t.total_harga
            FROM transaksi t
            JOIN customer c
                ON t.id_customer = c.id_customer
            WHERE t.id_transaksi = ?
            """;

        try {
            siapkanQuery(sql);
            st.setString(1, id);
            return st.executeQuery();
        } catch (SQLException e) {
            System.out.println("Gagal mencari transaksi: " + e.getMessage());
            return null;
        }
    }

    // 3. Mencari transaksi berdasarkan nama pelanggan atau ID pelanggan
    public ResultSet cariTransaksiByCustomer(String keyword) {
        String sql = """
            SELECT
                t.id_transaksi,
                c.nama_lengkap,
                t.tanggal_transaksi,
                t.total_harga
            FROM transaksi t
            JOIN customer c
                ON t.id_customer = c.id_customer
            WHERE c.nama_lengkap ILIKE ?
                OR t.id_customer ILIKE ?
            ORDER BY t.tanggal_transaksi DESC
            """;

        try {
            siapkanQuery(sql);
            st.setString(1, "%" + keyword + "%");
            st.setString(2, "%" + keyword + "%");
            return st.executeQuery();
        } catch (SQLException e) {
            System.out.println("Gagal mencari transaksi pelanggan: " + e.getMessage());
            return null;
        }
    }

    // 4. Menampilkan detail item transaksi
    public ResultSet tampilDetailTransaksi(String id) {
        String sql = """
            SELECT
                id_detail,
                id_transaksi,
                kode,
                nama_item,
                jenis_item,
                jumlah,
                harga_satuan,
                subtotal
            FROM detail_transaksi
            WHERE id_transaksi = ?
            ORDER BY id_detail
            """;

        try {
            siapkanQuery(sql);
            st.setString(1, id);
            return st.executeQuery();
        } catch (SQLException e) {
            System.out.println("Gagal menampilkan detail transaksi: " + e.getMessage());
            return null;
        }
    }

    // 5. Menampilkan pembayaran transaksi
    public ResultSet tampilPembayaran(String id) {
        String sql = """
            SELECT
                id_pembayaran,
                id_transaksi,
                metode_pembayaran,
                jumlah_dibayar,
                uang_diterima,
                uang_kembalian
            FROM pembayaran
            WHERE id_transaksi = ?
            ORDER BY id_pembayaran
            """;

        try {
            siapkanQuery(sql);
            st.setString(1, id);
            return st.executeQuery();
        } catch (SQLException e) {
            System.out.println("Gagal menampilkan pembayaran: " + e.getMessage());
            return null;
        }
    }
}
