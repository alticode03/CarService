
package koneksijdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataManagement implements AutoCloseable {

    private Connection conn;

    public DataManagement() {
        try {
            this.conn = new KoneksiSupabase().conn();
        } catch (SQLException e) {
            throw new IllegalStateException(
                "Gagal koneksi ke Supabase: " + e.getMessage(), e
            );
        }
    }

    // Menghitung jumlah seluruh transaksi
    public int getTotalTransactions() {
        String sql = "SELECT COUNT(*) FROM transaksi";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Gagal menghitung transaksi: " + e.getMessage());
        }

        return 0;
    }

    // Memeriksa apakah ID transaksi tersedia
    public boolean transaksiAda(String id) {
        String sql = """
            SELECT 1
            FROM transaksi
            WHERE id_transaksi = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Gagal mencari transaksi: " + e.getMessage());
            return false;
        }
    }

    // Menutup koneksi database
    @Override
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
