
import java.util.Scanner;

public class TransactionHistory {

    private DataManagement data;
    private Scanner input;

    public TransactionHistory(DataManagement data) {
        this.data = data;
        this.input = new Scanner(System.in);
    }

    // Menambahkan transaksi
    public void addTransaction() {

        System.out.println("\n=== TAMBAH TRANSAKSI ===");

        System.out.print("ID Transaksi: ");
        String id = input.nextLine();

        if (data.findTransaction(id) != null) {
            System.out.println("ID sudah digunakan!");
            return;
        }

        System.out.print("Nama Transaksi: ");
        String name = input.nextLine();

        System.out.print("Kategori: ");
        String category = input.nextLine();

        System.out.print("Jumlah: ");

        double amount;

        try {
            amount = Double.parseDouble(input.nextLine());

            if (amount <= 0) {
                System.out.println("Jumlah harus lebih dari 0!");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Jumlah tidak valid!");
            return;
        }

        System.out.println("Jenis Transaksi:");
        System.out.println("1. Pemasukan");
        System.out.println("2. Pengeluaran");
        System.out.print("Pilih: ");

        String choice = input.nextLine();

        String type;

        if (choice.equals("1")) {
            type = "Pemasukan";
        } else if (choice.equals("2")) {
            type = "Pengeluaran";
        } else {
            System.out.println("Pilihan tidak valid!");
            return;
        }

        System.out.print("Deskripsi: ");
        String description = input.nextLine();

        String date = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter
                .ofPattern("dd-MM-yyyy HH:mm:ss"));

        String[] transaction = {
            id, name, category,
            String.valueOf(amount),
            type, description, date
        };

        data.addTransaction(transaction);

        System.out.println("Transaksi berhasil ditambahkan!");
    }

    // Menampilkan seluruh transaksi
    public void displayHistory() {

        System.out.println("\n=== RIWAYAT TRANSAKSI ===");

        if (data.getAllTransactions().isEmpty()) {
            System.out.println("Belum ada transaksi.");
            return;
        }

        for (String[] t : data.getAllTransactions()) {

            System.out.println("-----------------------------");
            System.out.println("ID       : " + t[0]);
            System.out.println("Nama     : " + t[1]);
            System.out.println("Kategori : " + t[2]);
            System.out.println("Jumlah   : Rp " + t[3]);
            System.out.println("Jenis    : " + t[4]);
        }
    }

    // Mencari transaksi
    public void searchTransaction() {

        System.out.print("\nMasukkan ID atau Nama: ");
        String keyword = input.nextLine();

        boolean found = false;

        for (String[] t : data.getAllTransactions()) {

            if (t[0].equalsIgnoreCase(keyword) ||
                t[1].toLowerCase().contains(keyword.toLowerCase())) {

                System.out.println("\nTransaksi ditemukan!");
                System.out.println("ID    : " + t[0]);
                System.out.println("Nama  : " + t[1]);
                System.out.println("Jumlah: Rp " + t[3]);

                found = true;
            }
        }

        if (!found) {
            System.out.println("Transaksi tidak ditemukan!");
        }
    }

    // Melihat detail transaksi
    public void transactionDetail() {

        System.out.print("\nMasukkan ID Transaksi: ");
        String id = input.nextLine();

        String[] t = data.findTransaction(id);

        if (t == null) {
            System.out.println("Transaksi tidak ditemukan!");
            return;
        }

        System.out.println("\n===== DETAIL TRANSAKSI =====");
        System.out.println("ID          : " + t[0]);
        System.out.println("Nama        : " + t[1]);
        System.out.println("Kategori    : " + t[2]);
        System.out.println("Jumlah      : Rp " + t[3]);
        System.out.println("Jenis       : " + t[4]);
        System.out.println("Deskripsi   : " + t[5]);
        System.out.println("Tanggal     : " + t[6]);
        System.out.println("============================");
    }
}