package com.carservice;

import static com.carservice.menu.InputTerminal.readLine;

import com.carservice.menu.MenuCustomer;
import com.carservice.menu.MenuKategori;
import com.carservice.menu.MenuKoneksi;
import com.carservice.menu.MenuProdukJasa;
import com.carservice.menu.MenuTransaction;
import java.io.PrintStream;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out);
    }

    static void run(Scanner input, PrintStream output) {
        MenuCustomer customerMenu = new MenuCustomer();
        MenuKategori kategoriMenu = new MenuKategori();
        MenuProdukJasa produkJasaMenu = new MenuProdukJasa();
        MenuTransaction transactionMenu = new MenuTransaction();

        while (true) {
            output.println();
            output.println("=== CarService ===");
            output.println("1. CRUD pelanggan");
            output.println("2. CRUD kategori");
            output.println("3. CRUD produk/jasa");
            output.println("4. Tes koneksi JDBC dan cek tabel");
            output.println("5. Tes Supabase Auth API");
            output.println("6. Riwayat transaksi, struk, dan pengelolaan data");
            output.println("0. Keluar");

            String choice = readLine(input, output, "Pilih: ");

            if (choice == null) {
                return;
            }

            switch (choice.trim()) {
                case "1":
                    customerMenu.run(input, output);
                    break;
                case "2":
                    kategoriMenu.run(input, output);
                    break;
                case "3":
                    produkJasaMenu.run(input, output);
                    break;
                case "4":
                    MenuKoneksi.testDatabaseConnection(output);
                    break;
                case "5":
                    MenuKoneksi.testSupabaseAuth();
                    break;
                case "6":
                    transactionMenu.run(input, output);
                    break;
                case "0":
                    output.println("Selesai.");
                    return;
                default:
                    output.println("Pilihan tidak dikenal. Masukkan angka 0 sampai 6.");
            }
        }
    }
}
