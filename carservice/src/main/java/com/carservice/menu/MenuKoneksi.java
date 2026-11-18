package com.carservice.menu;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import koneksijdbc.KoneksiSupabase;

public final class MenuKoneksi {

    private static final List<String> EXPECTED_TABLES =
            List.of("kategori", "customer", "produk_jasa");

    private MenuKoneksi() {
    }

    public static void testSupabaseAuth() {
        new KoneksiSupabase().testKoneksi();
    }

    public static void testDatabaseConnection(PrintStream output) {
        try (Connection connection = new KoneksiSupabase().conn()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT 1");
                 ResultSet result = statement.executeQuery()) {
                if (!result.next() || result.getInt(1) != 1) {
                    throw new SQLException("Query pemeriksaan SELECT 1 gagal.");
                }
            }

            output.println("Koneksi JDBC berhasil ke "
                    + connection.getMetaData().getDatabaseProductName() + ".");

            Set<String> foundTables = new HashSet<>();
            String sql = "SELECT table_name FROM information_schema.tables "
                    + "WHERE table_schema = 'public' AND table_type = 'BASE TABLE' "
                    + "AND table_name IN (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 0; i < EXPECTED_TABLES.size(); i++) {
                    statement.setString(i + 1, EXPECTED_TABLES.get(i));
                }
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        foundTables.add(result.getString("table_name"));
                    }
                }
            }

            for (String table : EXPECTED_TABLES) {
                output.println((foundTables.contains(table) ? "[OK] " : "[BELUM ADA] ")
                        + "public." + table);
            }
        } catch (SQLException | IllegalStateException e) {
            output.println("Tes JDBC gagal: " + e.getMessage());
        }
    }
}
