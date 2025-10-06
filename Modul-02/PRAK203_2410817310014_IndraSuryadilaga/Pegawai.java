package PRAK203_2410817310014_IndraSuryadilaga;

//Pada baris ini terjadi error karena nama class tidak sesuai dengan yang dipanggil di Soal3Main (Employee =! Pegawai)
//public class Employee {
public class Pegawai {
 public String nama;

 // Pada baris ini terjadi error karena tipe data asal seharusnya String, bukan char
 // public char asal;
 public String asal;

 public String jabatan;
 public int umur = 17; // Menambahkan nilai default agar sesuai output "17 tahun"

 public String getNama() {
     return nama;
 }

 // Pada baris ini terjadi error karena tipe return method getAsal() adalah String, tapi variabel asal dideklarasikan sebagai char
 // public String getAsal() {
 //     return asal;
 // }
 public String getAsal() {
     return asal;
 }

 // Pada baris ini terjadi error karena parameter "j" tidak dideklarasikan, belum ada parameter di method
 // public void setJabatan() {
 //     this.jabatan = j;
 // }
 public void setJabatan(String j) {
     this.jabatan = j;
 }
}
