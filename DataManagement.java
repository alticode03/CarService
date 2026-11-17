
import java.util.ArrayList;
import java.util.HashMap;

public class DataManagement {

    // ArrayList untuk menyimpan seluruh transaksi
    private ArrayList<String[]> transactions;

    // HashMap untuk mencari transaksi berdasarkan ID
    private HashMap<String, String[]> transactionMap;

    public DataManagement() {
        transactions = new ArrayList<>();
        transactionMap = new HashMap<>();
    }

    // Menambahkan transaksi
    public void addTransaction(String[] transaction) {

        String id = transaction[0];

        if (transactionMap.containsKey(id)) {
            System.out.println("ID transaksi sudah ada!");
            return;
        }

        transactions.add(transaction);
        transactionMap.put(id, transaction);
    }

    // Mengambil seluruh transaksi
    public ArrayList<String[]> getAllTransactions() {
        return transactions;
    }

    // Mencari transaksi berdasarkan ID
    public String[] findTransaction(String id) {
        return transactionMap.get(id);
    }

    // Menghapus transaksi berdasarkan ID
    public void deleteTransaction(String id) {

        String[] transaction = transactionMap.remove(id);

        if (transaction != null) {
            transactions.remove(transaction);
            System.out.println("Transaksi berhasil dihapus!");
        } else {
            System.out.println("Transaksi tidak ditemukan!");
        }
    }

    // Menghitung jumlah transaksi
    public int getTotalTransactions() {
        return transactions.size();
    }
}