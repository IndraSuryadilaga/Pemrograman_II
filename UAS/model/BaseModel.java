package model;

/**
 * Abstract Class yang menjadi induk semua Model.
 * Menerapkan prinsip Abstraction.
 */
public abstract class BaseModel {
    // Encapsulation: Access modifier protected agar bisa diakses anak-anaknya (subclass)
    protected int id;

    // Constructor
    public BaseModel(int id) {
        this.id = id;
    }
    
    // Polymorphism (Overloading Constructor) - Untuk objek baru yang belum punya ID
    public BaseModel() {
        this.id = 0;
    }

    // Getter & Setter (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Abstract method (Memaksa semua anak class punya method ini)
    // Contoh: Untuk keperluan debugging log
    public abstract String toString();
}
