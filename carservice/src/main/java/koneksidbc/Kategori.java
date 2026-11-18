package koneksidbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Kategori {

    public Statement st;

    public Kategori() {
        try {
            this.st = new KoneksiSupabase().conn().createStatement();
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal koneksi ke Supabase: " + e.getMessage(), e);
        }
    }

    private String esc(String teks) {
        return teks == null ? "" : teks.replace("'", "''");
    }

    public boolean tambah(String namaKategori) {
        String sql = "INSERT INTO Kategori (nama_kategori) VALUES ('"
                + esc(namaKategori) + "')";
        try {
            return st.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            System.out.println("Gagal menambah kategori: " + e.getMessage());
            return false;
        }
    }

    public boolean tambah(int idKategori, String namaKategori) {
        String sql = "INSERT INTO Kategori (id_kategori, nama_kategori) VALUES ("
                + idKategori + ", '" + esc(namaKategori) + "')";
        try {
            return st.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            System.out.println("Gagal menambah kategori: " + e.getMessage());
            return false;
        }
    }

    public ResultSet tampil() {
        String sql = "SELECT id_kategori, nama_kategori FROM Kategori ORDER BY id_kategori";
        try {
            return st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Gagal menampilkan kategori: " + e.getMessage());
            return null;
        }
    }

    public ResultSet cariById(int idKategori) {
        String sql = "SELECT id_kategori, nama_kategori FROM Kategori WHERE id_kategori = " + idKategori;
        try {
            return st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Gagal mencari kategori: " + e.getMessage());
            return null;
        }
    }

    public ResultSet cariByNama(String keyword) {
        String sql = "SELECT id_kategori, nama_kategori FROM Kategori "
                + "WHERE nama_kategori LIKE '%" + esc(keyword) + "%' ORDER BY id_kategori";
        try {
            return st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Gagal mencari kategori: " + e.getMessage());
            return null;
        }
    }

    public boolean ubah(int idKategori, String namaBaru) {
        String sql = "UPDATE Kategori SET nama_kategori = '" + esc(namaBaru)
                + "' WHERE id_kategori = " + idKategori;
        try {
            return st.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            System.out.println("Gagal mengubah kategori: " + e.getMessage());
            return false;
        }
    }

    public boolean hapus(int idKategori) {
        String sql = "DELETE FROM Kategori WHERE id_kategori = " + idKategori;
        try {
            return st.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            System.out.println("Gagal menghapus kategori: " + e.getMessage());
            return false;
        }
    }
}