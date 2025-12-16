package dao;

import java.util.List;

// Interface generic untuk operasi CRUD standar menggunakan generics dan polymorphism (Interface-based)
public interface DaoInterface<T> {
    // Mengambil semua data dari database
    List<T> getAll();
    // Mengambil satu data berdasarkan ID
    T get(int id);
    // Menambahkan data baru ke database
    boolean add(T t);
    // Mengupdate data yang sudah ada di database
    boolean update(T t);
    // Menghapus data dari database berdasarkan ID
    boolean delete(int id);
}
