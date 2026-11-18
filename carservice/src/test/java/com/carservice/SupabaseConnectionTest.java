package com.carservice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import koneksijdbc.KoneksiSupabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

class SupabaseConnectionTest {

    @Test
    @EnabledIfSystemProperty(named = "supabase.integration", matches = "true")
    void connectsAndFindsApplicationTables() throws Exception {
        Set<String> expectedTables = Set.of("kategori", "customer", "produk_jasa");

        try (Connection connection = new KoneksiSupabase().conn();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT table_name FROM information_schema.tables "
                             + "WHERE table_schema = 'public' "
                             + "AND table_type = 'BASE TABLE'")) {
            assertTrue(connection.isValid(5), "Koneksi JDBC harus aktif.");

            Set<String> foundTables = new HashSet<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    foundTables.add(result.getString("table_name"));
                }
            }

            assertTrue(foundTables.containsAll(expectedTables),
                    "Tabel aplikasi yang diharapkan harus tersedia: " + expectedTables);
        }
    }
}
