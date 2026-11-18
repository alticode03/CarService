package com.carservice.menu;

import static com.carservice.menu.InputTerminal.closeDao;
import static com.carservice.menu.InputTerminal.confirmDelete;
import static com.carservice.menu.InputTerminal.printRows;
import static com.carservice.menu.InputTerminal.readLine;
import static com.carservice.menu.InputTerminal.readRequiredText;
import static com.carservice.menu.InputTerminal.reportUpdate;

import java.io.PrintStream;
import java.util.Scanner;
import koneksijdbc.Customer;

public final class MenuCustomer {

    public void run(Scanner input, PrintStream output) {
        Customer customer;
        try {
            customer = new Customer();
        } catch (IllegalStateException e) {
            output.println("Gagal membuka menu pelanggan: " + e.getMessage());
            return;
        }

        try {
            while (true) {
                output.println();
                output.println("=== CRUD Pelanggan ===");
                output.println("1. Tampilkan semua pelanggan");
                output.println("2. Cari berdasarkan ID");
                output.println("3. Cari berdasarkan nama/nomor HP");
                output.println("4. Tambah pelanggan");
                output.println("5. Ubah pelanggan");
                output.println("6. Hapus pelanggan");
                output.println("0. Kembali");
                String choice = readLine(input, output, "Pilih: ");
                if (choice == null || choice.trim().equals("0")) {
                    return;
                }

                switch (choice.trim()) {
                    case "1":
                        printRows(customer.tampilSemuaCustomer(),
                                new String[]{"id_customer", "nama_lengkap",
                                    "no_hp_whatsapp", "alamat"}, output);
                        break;
                    case "2": {
                        String id = readRequiredText(input, output, "ID pelanggan: ");
                        if (id != null) {
                            printRows(customer.cariCustomerById(id),
                                    new String[]{"id_customer", "nama_lengkap",
                                        "no_hp_whatsapp", "alamat"}, output);
                        }
                        break;
                    }
                    case "3": {
                        String keyword = readRequiredText(input, output, "Kata kunci: ");
                        if (keyword != null) {
                            printRows(customer.cariCustomer(keyword),
                                    new String[]{"id_customer", "nama_lengkap",
                                        "no_hp_whatsapp", "alamat"}, output);
                        }
                        break;
                    }
                    case "4":
                        addCustomer(customer, input, output);
                        break;
                    case "5":
                        updateCustomer(customer, input, output);
                        break;
                    case "6": {
                        String id = readRequiredText(input, output, "ID pelanggan yang dihapus: ");
                        if (id != null && confirmDelete(input, output, "pelanggan " + id)) {
                            reportUpdate(customer.hapusCustomer(id),
                                    "Pelanggan dihapus.", output);
                        }
                        break;
                    }
                    default:
                        output.println("Pilihan tidak dikenal.");
                }
            }
        } finally {
            closeDao(customer.st, output);
        }
    }

    private void addCustomer(Customer customer, Scanner input, PrintStream output) {
        String id = readRequiredText(input, output, "ID pelanggan: ");
        if (id == null) return;
        String name = readRequiredText(input, output, "Nama lengkap: ");
        if (name == null) return;
        String phone = readLine(input, output, "Nomor HP/WhatsApp (boleh kosong): ");
        if (phone == null) return;
        String address = readLine(input, output, "Alamat (boleh kosong): ");
        if (address == null) return;

        reportUpdate(customer.tambahCustomer(id, name, phone, address),
                "Pelanggan ditambahkan.", output);
    }

    private void updateCustomer(Customer customer, Scanner input, PrintStream output) {
        String id = readRequiredText(input, output, "ID pelanggan yang diubah: ");
        if (id == null) return;
        String name = readRequiredText(input, output, "Nama lengkap baru: ");
        if (name == null) return;
        String phone = readLine(input, output, "Nomor HP/WhatsApp baru (boleh kosong): ");
        if (phone == null) return;
        String address = readLine(input, output, "Alamat baru (boleh kosong): ");
        if (address == null) return;

        reportUpdate(customer.ubahCustomer(id, name, phone, address),
                "Pelanggan diperbarui.", output);
    }
}
