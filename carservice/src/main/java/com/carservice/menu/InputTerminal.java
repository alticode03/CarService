package com.carservice.menu;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Scanner;

public final class InputTerminal {

    private InputTerminal() {
    }

    public static String readLine(Scanner input, PrintStream output, String prompt) {
        output.print(prompt);
        return input.hasNextLine() ? input.nextLine() : null;
    }

    public static String readRequiredText(Scanner input, PrintStream output, String prompt) {
        while (true) {
            String value = readLine(input, output, prompt);
            if (value == null) {
                return null;
            }
            if (!value.trim().isEmpty()) {
                return value.trim();
            }
            output.println("Nilai tidak boleh kosong.");
        }
    }

    public static Integer readInteger(Scanner input, PrintStream output, String prompt) {
        while (true) {
            String value = readLine(input, output, prompt);
            if (value == null) {
                return null;
            }
            try {
                return Integer.valueOf(value.trim());
            } catch (NumberFormatException e) {
                output.println("Masukkan angka bulat yang valid.");
            }
        }
    }

    public static Optional<Integer> readOptionalInteger(
            Scanner input, PrintStream output, String prompt) {
        while (true) {
            String value = readLine(input, output, prompt);
            if (value == null) {
                return null;
            }
            if (value.trim().isEmpty()) {
                return Optional.empty();
            }
            try {
                return Optional.of(Integer.valueOf(value.trim()));
            } catch (NumberFormatException e) {
                output.println("Masukkan angka bulat atau kosongkan.");
            }
        }
    }

    public static String readJenis(Scanner input, PrintStream output) {
        while (true) {
            String type = readRequiredText(input, output, "Jenis (Produk/Jasa): ");
            if (type == null) {
                return null;
            }
            if (type.equalsIgnoreCase("Produk")) {
                return "Produk";
            }
            if (type.equalsIgnoreCase("Jasa")) {
                return "Jasa";
            }
            output.println("Jenis harus Produk atau Jasa.");
        }
    }

    public static boolean confirmDelete(Scanner input, PrintStream output, String item) {
        String confirmation = readLine(input, output,
                "Ketik YA untuk menghapus " + item + ": ");
        if (confirmation != null && confirmation.trim().equalsIgnoreCase("YA")) {
            return true;
        }
        output.println("Penghapusan dibatalkan.");
        return false;
    }

    public static void printRows(ResultSet rows, String[] columns, PrintStream output) {
        if (rows == null) {
            output.println("Query gagal; tidak ada hasil yang dapat ditampilkan.");
            return;
        }

        try (ResultSet result = rows) {
            boolean found = false;
            while (result.next()) {
                found = true;
                for (String column : columns) {
                    output.print(column + "=" + result.getString(column) + " | ");
                }
                output.println();
            }
            if (!found) {
                output.println("Data tidak ditemukan.");
            }
        } catch (SQLException e) {
            output.println("Gagal membaca hasil query: " + e.getMessage());
        }
    }

    public static void reportUpdate(boolean success, String successMessage, PrintStream output) {
        output.println(success ? successMessage : "Operasi gagal atau data tidak ditemukan.");
    }

    public static void closeDao(Statement statement, PrintStream output) {
        if (statement == null) {
            return;
        }

        SQLException failure = null;
        try {
            Connection connection = statement.getConnection();
            try {
                statement.close();
            } catch (SQLException e) {
                failure = e;
            }
            try {
                connection.close();
            } catch (SQLException e) {
                if (failure == null) {
                    failure = e;
                } else {
                    failure.addSuppressed(e);
                }
            }
        } catch (SQLException e) {
            failure = e;
        }
        if (failure != null) {
            output.println("Gagal menutup koneksi database: " + failure.getMessage());
        }
    }
}
