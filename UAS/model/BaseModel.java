package model;

// Abstract class BaseModel sebagai parent class untuk semua model (Inheritance dan Abstraction)
public abstract class BaseModel {
    // Field protected agar bisa diakses oleh subclass (Encapsulation)
    protected int id;

    // Constructor untuk membuat objek dengan ID dari database
    public BaseModel(int id) {
        this.id = id;
    }
    
    // Constructor untuk membuat objek baru tanpa ID (Constructor Overloading - Polymorphism)
    public BaseModel() {
        this.id = 0;
    }

    // Getter untuk mengakses ID (Encapsulation)
    public int getId() {
        return id;
    }

    // Setter untuk mengubah ID (Encapsulation)
    public void setId(int id) {
        this.id = id;
    }

    // Abstract method yang harus diimplementasikan oleh subclass (Abstraction)
    public abstract String toString();
}
