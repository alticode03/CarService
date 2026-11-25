
package koneksijdbc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

public class ReceiptManagement implements AutoCloseable {

    private Connection conn;

    public ReceiptManagement() {
        try {
            this.conn = new KoneksiSupabase().conn();
        } catch (SQLException e) {
            throw new IllegalStateException(
                "Gagal koneksi ke Supabase: " + e.getMessage(), e
            );
        }
    }

    // Membuat format struk berdasarkan ID transaksi
    public String createReceipt(String id) {

        StringBuilder receipt = new StringBuilder();
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(
            new Locale("id", "ID")
        );

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
            // Mengambil informasi utama transaksi
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Transaksi tidak ditemukan.");
                        return null;
                    }

                    receipt.append("================================\n");
                    receipt.append("          CAR SERVICE           \n");
                    receipt.append("          STRUK TRANSAKSI       \n");
                    receipt.append("================================\n");
                    receipt.append("ID Transaksi : ")
                           .append(rs.getString("id_transaksi")).append("\n");
                    receipt.append("Pelanggan    : ")
                           .append(rs.getString("nama_lengkap")).append("\n");
                    receipt.append("Tanggal      : ")
                           .append(rs.getTimestamp("tanggal_transaksi")).append("\n");
                    receipt.append("--------------------------------\n");
                    receipt.append("DETAIL PEMBELIAN\n");
                }
            }

            // Mengambil daftar produk dan jasa
            String detailSql = """
                SELECT nama_item, jenis_item, jumlah,
                       harga_satuan, subtotal
                FROM detail_transaksi
                WHERE id_transaksi = ?
                ORDER BY id_detail
                """;

            try (PreparedStatement ps = conn.prepareStatement(detailSql)) {
                ps.setString(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        receipt.append(rs.getString("nama_item"))
                               .append(" (")
                               .append(rs.getString("jenis_item"))
                               .append(")\n");
                        receipt.append("  ")
                               .append(rs.getInt("jumlah"))
                               .append(" x ")
                               .append(rupiah.format(rs.getLong("harga_satuan")))
                               .append(" = ")
                               .append(rupiah.format(rs.getLong("subtotal")))
                               .append("\n");
                    }
                }
            }

            // Mengambil total transaksi
            String totalSql = """
                SELECT total_harga
                FROM transaksi
                WHERE id_transaksi = ?
                """;

            long total = 0;

            try (PreparedStatement ps = conn.prepareStatement(totalSql)) {
                ps.setString(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        total = rs.getLong("total_harga");
                    }
                }
            }

            receipt.append("--------------------------------\n");
            receipt.append("TOTAL: ")
                   .append(rupiah.format(total)).append("\n");

            // Mengambil informasi pembayaran
            String paymentSql = """
                SELECT metode_pembayaran, jumlah_dibayar,
                       uang_diterima, uang_kembalian
                FROM pembayaran
                WHERE id_transaksi = ?
                ORDER BY id_pembayaran
                """;

            receipt.append("\nPEMBAYARAN\n");

            try (PreparedStatement ps = conn.prepareStatement(paymentSql)) {
                ps.setString(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        receipt.append(rs.getString("metode_pembayaran"))
                               .append(" : ")
                               .append(rupiah.format(rs.getLong("jumlah_dibayar")))
                               .append("\n");

                        if ("Tunai".equalsIgnoreCase(
                                rs.getString("metode_pembayaran"))) {
                            receipt.append("Uang diterima : ")
                                   .append(rupiah.format(rs.getLong("uang_diterima")))
                                   .append("\n");
                            receipt.append("Kembalian     : ")
                                   .append(rupiah.format(rs.getLong("uang_kembalian")))
                                   .append("\n");
                        }
                    }
                }
            }

            receipt.append("================================\n");
            receipt.append("       TERIMA KASIH             \n");
            receipt.append("================================\n");

            return receipt.toString();

        } catch (SQLException e) {
            System.out.println("Gagal membuat struk: " + e.getMessage());
            return null;
        }
    }

    // Menampilkan struk ke terminal
    public void printReceipt(String id) {
        String receipt = createReceipt(id);

        if (receipt != null) {
            System.out.println(receipt);
        }
    }

    // Menyimpan struk sebagai file TXT
    public void saveReceipt(String id) {
        String receipt = createReceipt(id);

        if (receipt == null) {
            return;
        }

        try {
            String safeId = id.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = "Receipt_" + safeId + ".txt";

            Files.writeString(
                Path.of(fileName),
                receipt,
                StandardCharsets.UTF_8
            );

            System.out.println("Struk berhasil disimpan: " + fileName);

        } catch (IOException e) {
            System.out.println("Gagal menyimpan struk: " + e.getMessage());
        }
    }

    @Override
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
