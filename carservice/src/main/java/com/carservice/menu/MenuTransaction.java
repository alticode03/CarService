
package com.carservice.menu;

import static com.carservice.menu.InputTerminal.printRows;
import static com.carservice.menu.InputTerminal.readLine;
import static com.carservice.menu.InputTerminal.readRequiredText;

import java.io.PrintStream;
import java.util.Scanner;

import koneksijdbc.TransactionHistory;
import koneksijdbc.ReceiptManagement;
import koneksijdbc.DataManagement;

public final class MenuTransaction {

    public void run(Scanner input, PrintStream output) {

        TransactionHistory history;

        try {
            history = new TransactionHistory();
        } catch (IllegalStateException e) {
            output.println("Gagal membuka riwayat transaksi: " + e.getMessage());
            return;
        }

        try (
            ReceiptManagement receipt = new ReceiptManagement();
            DataManagement data = new DataManagement()
        ) {
            while (true) {
                output.println();
                output.println("=== TRANSACTION MANAGEMENT ===");
                output.println("1. Tampilkan semua transaksi");
                output.println("2. Cari transaksi berdasarkan ID");
                output.println("3. Cari transaksi berdasarkan pelanggan");
                output.println("4. Tampilkan detail transaksi");
                output.println("5. Tampilkan pembayaran transaksi");
                output.println("6. Cetak struk transaksi");
                output.println("7. Simpan struk ke file TXT");
                output.println("8. Hitung jumlah seluruh transaksi");
                output.println("9. Periksa ID transaksi");
                output.println("0. Kembali");

                String choice = readLine(input, output, "Pilih: ");

                if (choice == null || choice.trim().equals("0")) {
                    return;
                }

                switch (choice.trim()) {
                    case "1":
                        printRows(
                            history.tampilSemuaTransaksi(),
                            new String[]{
                                "id_transaksi",
                                "nama_lengkap",
                                "tanggal_transaksi",
                                "total_harga"
                            },
                            output
                        );
                        break;

                    case "2": {
                        String id = readRequiredText(
                            input, output, "ID transaksi: "
                        );

                        if (id != null) {
                            printRows(
                                history.cariTransaksiById(id),
                                new String[]{
                                    "id_transaksi",
                                    "nama_lengkap",
                                    "tanggal_transaksi",
                                    "total_harga"
                                },
                                output
                            );
                        }
                        break;
                    }

                    case "3": {
                        String keyword = readRequiredText(
                            input, output, "Nama atau ID pelanggan: "
                        );

                        if (keyword != null) {
                            printRows(
                                history.cariTransaksiByCustomer(keyword),
                                new String[]{
                                    "id_transaksi",
                                    "nama_lengkap",
                                    "tanggal_transaksi",
                                    "total_harga"
                                },
                                output
                            );
                        }
                        break;
                    }

                    case "4": {
                        String id = readRequiredText(
                            input, output, "ID transaksi: "
                        );

                        if (id != null) {
                            printRows(
                                history.tampilDetailTransaksi(id),
                                new String[]{
                                    "id_detail",
                                    "id_transaksi",
                                    "kode",
                                    "nama_item",
                                    "jenis_item",
                                    "jumlah",
                                    "harga_satuan",
                                    "subtotal"
                                },
                                output
                            );
                        }
                        break;
                    }

                    case "5": {
                        String id = readRequiredText(
                            input, output, "ID transaksi: "
                        );

                        if (id != null) {
                            printRows(
                                history.tampilPembayaran(id),
                                new String[]{
                                    "id_pembayaran",
                                    "id_transaksi",
                                    "metode_pembayaran",
                                    "jumlah_dibayar",
                                    "uang_diterima",
                                    "uang_kembalian"
                                },
                                output
                            );
                        }
                        break;
                    }

                    case "6": {
                        String id = readRequiredText(
                            input, output, "ID transaksi: "
                        );

                        if (id != null) {
                            receipt.printReceipt(id);
                        }
                        break;
                    }

                    case "7": {
                        String id = readRequiredText(
                            input, output, "ID transaksi: "
                        );

                        if (id != null) {
                            receipt.saveReceipt(id);
                        }
                        break;
                    }

                    case "8":
                        output.println(
                            "Jumlah seluruh transaksi: "
                            + data.getTotalTransactions()
                        );
                        break;

                    case "9": {
                        String id = readRequiredText(
                            input, output, "ID transaksi: "
                        );

                        if (id != null) {
                            output.println(
                                data.transaksiAda(id)
                                    ? "Transaksi ditemukan."
                                    : "Transaksi tidak ditemukan."
                            );
                        }
                        break;
                    }

                    default:
                        output.println("Pilihan tidak dikenal.");
                }
            }

        } catch (Exception e) {
            output.println(
                "Terjadi kesalahan pada menu transaksi: " + e.getMessage()
            );
        } finally {
            InputTerminal.closeDao(history.st, output);
        }
    }
}
