
import java.io.FileWriter;
import java.io.IOException;

public class ReceiptManagement {

    private DataManagement data;

    public ReceiptManagement(DataManagement data) {
        this.data = data;
    }

    // Membuat struk transaksi
    public String createReceipt(String id) {

        String[] t = data.findTransaction(id);

        if (t == null) {
            System.out.println("Transaksi tidak ditemukan!");
            return null;
        }

        String receipt = "";

        receipt += "================================\n";
        receipt += "         STRUK TRANSAKSI\n";
        receipt += "================================\n";
        receipt += "ID Transaksi : " + t[0] + "\n";
        receipt += "Nama         : " + t[1] + "\n";
        receipt += "Kategori     : " + t[2] + "\n";
        receipt += "Jenis        : " + t[4] + "\n";
        receipt += "Tanggal      : " + t[6] + "\n";
        receipt += "--------------------------------\n";
        receipt += "Jumlah       : Rp " + t[3] + "\n";
        receipt += "Deskripsi    : " + t[5] + "\n";
        receipt += "================================\n";
        receipt += "       TERIMA KASIH\n";
        receipt += "================================\n";

        return receipt;
    }

    // Mencetak struk ke layar
    public void printReceipt(String id) {

        String receipt = createReceipt(id);

        if (receipt == null) {
            return;
        }

        System.out.println("\n" + receipt);
    }

    // Menyimpan struk ke file
    public void saveReceipt(String id) {

        String receipt = createReceipt(id);

        if (receipt == null) {
            return;
        }

        try {
            String fileName = "Receipt_" + id + ".txt";

            FileWriter writer = new FileWriter(fileName);

            writer.write(receipt);
            writer.close();

            System.out.println(
                "Struk berhasil disimpan: " + fileName
            );

        } catch (IOException e) {
            System.out.println("Gagal menyimpan struk!");
        }
    }
}