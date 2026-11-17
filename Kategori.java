/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package koneksidbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author ASUS
 */
public class Kategori {

    public Statement st;

    // Konstruktor: otomatis menyiapkan koneksi sehingga st langsung bisa dipakai
    public Kategori() {
        KoneksiJDBC kj = new KoneksiJDBC();
        kj.config();
        this.st = kj.stm;
    }

    // Menghindari error/injeksi akibat tanda petik tunggal pada input teks
    private String esc(String teks) {
        return teks == null ? "" : teks.replace("'", "''");
    }

    // ===================== CREATE =====================
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

    // ===================== READ (semua data) =====================
    public ResultSet tampil() {
        String sql = "SELECT id_kategori, nama_kategori FROM Kategori ORDER BY id_kategori";
        try {
            return st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Gagal menampilkan kategori: " + e.getMessage());
            return null;
        }
    }

    // ===================== READ (berdasarkan id) =====================
    public ResultSet cariById(int idKategori) {
        String sql = "SELECT id_kategori, nama_kategori FROM Kategori WHERE id_kategori = " + idKategori;
        try {
            return st.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Gagal mencari kategori: " + e.getMessage());
            return null;
        }
    }

    // ===================== READ (berdasarkan nama, pencarian sebagian) =====================
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

    // ===================== UPDATE =====================
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

    // ===================== DELETE =====================
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