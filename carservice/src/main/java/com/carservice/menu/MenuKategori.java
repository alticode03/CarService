package com.carservice.menu;

import static com.carservice.menu.InputTerminal.closeDao;
import static com.carservice.menu.InputTerminal.confirmDelete;
import static com.carservice.menu.InputTerminal.printRows;
import static com.carservice.menu.InputTerminal.readInteger;
import static com.carservice.menu.InputTerminal.readLine;
import static com.carservice.menu.InputTerminal.readRequiredText;
import static com.carservice.menu.InputTerminal.reportUpdate;

import java.io.PrintStream;
import java.util.Scanner;
import koneksijdbc.Kategori;

public final class MenuKategori {

    public void run(Scanner input, PrintStream output) {
        Kategori kategori;
        try {
            kategori = new Kategori();
        } catch (IllegalStateException e) {
            output.println("Gagal membuka menu kategori: " + e.getMessage());
            return;
        }

        try {
            while (true) {
                output.println();
                output.println("=== CRUD Kategori ===");
                output.println("1. Tampilkan semua kategori");
                output.println("2. Cari berdasarkan ID");
                output.println("3. Cari berdasarkan nama");
                output.println("4. Tambah kategori");
                output.println("5. Ubah kategori");
                output.println("6. Hapus kategori");
                output.println("0. Kembali");
                String choice = readLine(input, output, "Pilih: ");
                if (choice == null || choice.trim().equals("0")) {
                    return;
                }

                switch (choice.trim()) {
                    case "1":
                        printRows(kategori.tampil(),
                                new String[]{"id_kategori", "nama_kategori"}, output);
                        break;
                    case "2": {
                        Integer id = readInteger(input, output, "ID kategori: ");
                        if (id != null) {
                            printRows(kategori.cariById(id),
                                    new String[]{"id_kategori", "nama_kategori"}, output);
                        }
                        break;
                    }
                    case "3": {
                        String keyword = readRequiredText(input, output, "Nama/kata kunci: ");
                        if (keyword != null) {
                            printRows(kategori.cariByNama(keyword),
                                    new String[]{"id_kategori", "nama_kategori"}, output);
                        }
                        break;
                    }
                    case "4": {
                        String name = readRequiredText(input, output, "Nama kategori: ");
                        if (name != null) {
                            reportUpdate(kategori.tambah(name),
                                    "Kategori ditambahkan.", output);
                        }
                        break;
                    }
                    case "5": {
                        Integer id = readInteger(input, output, "ID kategori yang diubah: ");
                        if (id != null) {
                            String name = readRequiredText(input, output, "Nama kategori baru: ");
                            if (name != null) {
                                reportUpdate(kategori.ubah(id, name),
                                        "Kategori diperbarui.", output);
                            }
                        }
                        break;
                    }
                    case "6": {
                        Integer id = readInteger(input, output, "ID kategori yang dihapus: ");
                        if (id != null && confirmDelete(input, output, "kategori " + id)) {
                            reportUpdate(kategori.hapus(id), "Kategori dihapus.", output);
                        }
                        break;
                    }
                    default:
                        output.println("Pilihan tidak dikenal.");
                }
            }
        } finally {
            closeDao(kategori.st, output);
        }
    }
}
