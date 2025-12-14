package dao;

import java.util.List;

/**
 * Interface Generic untuk operasi CRUD standar.
 * Menerapkan konsep Polymorphism (Interface-based).
 * @param <T> Tipe Model (misal: Team, Player)
 */
public interface DaoInterface<T> {
    List<T> getAll();       // Ambil semua data
    T get(int id);          // Ambil satu data berdasarkan ID
    boolean add(T t);       // Tambah data
    boolean update(T t);    // Edit data
    boolean delete(int id); // Hapus data
}
