package com.carservice.menu;

import static com.carservice.menu.InputTerminal.closeDao;
import static com.carservice.menu.InputTerminal.confirmDelete;
import static com.carservice.menu.InputTerminal.printRows;
import static com.carservice.menu.InputTerminal.readInteger;
import static com.carservice.menu.InputTerminal.readJenis;
import static com.carservice.menu.InputTerminal.readLine;
import static com.carservice.menu.InputTerminal.readOptionalInteger;
import static com.carservice.menu.InputTerminal.readRequiredText;
import static com.carservice.menu.InputTerminal.reportUpdate;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;
import koneksidbc.Produk_Jasa;

public final class MenuProdukJasa {

    private static final String[] PRODUCT_COLUMNS = {
        "kode", "id_kategori", "nama_kategori", "nama_produk_jasa",
        "merek", "harga", "stok", "jenis"
    };

    public void run(Scanner input, PrintStream output) {
        Produk_Jasa produkJasa;
        try {
            produkJasa = new Produk_Jasa();
        } catch (IllegalStateException e) {
            output.println("Gagal membuka menu produk/jasa: " + e.getMessage());
            return;
        }

        try {
            while (true) {
                output.println();
                output.println("=== CRUD Produk/Jasa ===");
                output.println("1. Tampilkan semua produk/jasa");
                output.println("2. Cari berdasarkan kode");
                output.println("3. Cari berdasarkan nama/merek");
                output.println("4. Filter berdasarkan jenis");
                output.println("5. Filter berdasarkan kategori");
                output.println("6. Tambah produk/jasa");
                output.println("7. Ubah produk/jasa");
                output.println("8. Hapus produk/jasa");
                output.println("9. Buat kode otomatis");
                output.println("10. Kurangi stok produk");
                output.println("0. Kembali");
                String choice = readLine(input, output, "Pilih: ");
                if (choice == null || choice.trim().equals("0")) {
                    return;
                }

                switch (choice.trim()) {
                    case "1":
                        printRows(produkJasa.tampilSemua(), PRODUCT_COLUMNS, output);
                        break;
                    case "2": {
                        String code = readRequiredText(input, output, "Kode produk/jasa: ");
                        if (code != null) {
                            printRows(produkJasa.cariByKode(code), PRODUCT_COLUMNS, output);
                        }
                        break;
                    }
                    case "3": {
                        String keyword = readRequiredText(input, output, "Kata kunci: ");
                        if (keyword != null) {
                            printRows(produkJasa.cari(keyword), PRODUCT_COLUMNS, output);
                        }
                        break;
                    }
                    case "4": {
                        String type = readJenis(input, output);
                        if (type != null) {
                            printRows(produkJasa.tampilByJenis(type), PRODUCT_COLUMNS, output);
                        }
                        break;
                    }
                    case "5": {
                        Integer categoryId = readInteger(input, output, "ID kategori: ");
                        if (categoryId != null) {
                            printRows(produkJasa.tampilByKategori(categoryId),
                                    PRODUCT_COLUMNS, output);
                        }
                        break;
                    }
                    case "6":
                        addProductJasa(produkJasa, input, output);
                        break;
                    case "7":
                        updateProductJasa(produkJasa, input, output);
                        break;
                    case "8": {
                        String code = readRequiredText(input, output, "Kode yang dihapus: ");
                        if (code != null && confirmDelete(input, output, "produk/jasa " + code)) {
                            reportUpdate(produkJasa.hapusProdukJasa(code),
                                    "Produk/jasa dihapus.", output);
                        }
                        break;
                    }
                    case "9": {
                        String type = readJenis(input, output);
                        if (type != null) {
                            String code = produkJasa.buatKodeBaru(type);
                            output.println(code == null
                                    ? "Kode gagal dibuat."
                                    : "Kode berikutnya: " + code);
                        }
                        break;
                    }
                    case "10":
                        reduceStock(produkJasa, input, output);
                        break;
                    default:
                        output.println("Pilihan tidak dikenal.");
                }
            }
        } finally {
            closeDao(produkJasa.st, output);
        }
    }

    private void addProductJasa(Produk_Jasa dao, Scanner input, PrintStream output) {
        String code = readRequiredText(input, output, "Kode produk/jasa: ");
        if (code == null) return;
        Integer categoryId = readInteger(input, output, "ID kategori: ");
        if (categoryId == null) return;
        String name = readRequiredText(input, output, "Nama produk/jasa: ");
        if (name == null) return;
        String brand = readLine(input, output, "Merek (boleh kosong): ");
        if (brand == null) return;
        Integer price = readInteger(input, output, "Harga (angka bulat): ");
        if (price == null) return;
        Optional<Integer> stock = readOptionalInteger(input, output,
                "Stok (kosongkan jika tidak berlaku): ");
        if (stock == null) return;
        String type = readJenis(input, output);
        if (type == null) return;

        reportUpdate(dao.tambahProdukJasa(code, categoryId, name, brand, price,
                stock.orElse(null), type), "Produk/jasa ditambahkan.", output);
    }

    private void updateProductJasa(Produk_Jasa dao, Scanner input, PrintStream output) {
        String code = readRequiredText(input, output, "Kode produk/jasa yang diubah: ");
        if (code == null) return;
        Integer categoryId = readInteger(input, output, "ID kategori baru: ");
        if (categoryId == null) return;
        String name = readRequiredText(input, output, "Nama produk/jasa baru: ");
        if (name == null) return;
        String brand = readLine(input, output, "Merek baru (boleh kosong): ");
        if (brand == null) return;
        Integer price = readInteger(input, output, "Harga baru (angka bulat): ");
        if (price == null) return;
        Optional<Integer> stock = readOptionalInteger(input, output,
                "Stok baru (kosongkan jika tidak berlaku): ");
        if (stock == null) return;
        String type = readJenis(input, output);
        if (type == null) return;

        reportUpdate(dao.ubahProdukJasa(code, categoryId, name, brand, price,
                stock.orElse(null), type), "Produk/jasa diperbarui.", output);
    }

    private void reduceStock(Produk_Jasa dao, Scanner input, PrintStream output) {
        String code = readRequiredText(input, output, "Kode produk: ");
        if (code == null) return;
        Integer quantity = readInteger(input, output, "Jumlah yang dikurangi: ");
        if (quantity == null) return;
        if (quantity <= 0) {
            output.println("Jumlah harus lebih dari 0.");
            return;
        }
        reportUpdate(dao.kurangiStok(code, quantity), "Stok berhasil dikurangi.", output);
    }
}
