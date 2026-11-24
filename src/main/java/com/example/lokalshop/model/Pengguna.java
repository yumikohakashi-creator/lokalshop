package com.example.lokalshop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pengguna")
public class Pengguna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pengguna")
    private Integer idPengguna;

    private String nama;
    private String email;
    private String password;

    @Column(name = "nomor_telepon")
    private String nomorTelepon;

    @Column(name = "koordinat_lokasi")
    private String koordinatLokasi;

    // Getter & Setter
    public Integer getIdPengguna() { return idPengguna; }
    public void setIdPengguna(Integer idPengguna) { this.idPengguna = idPengguna; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }
    public String getKoordinatLokasi() { return koordinatLokasi; }
    public void setKoordinatLokasi(String koordinatLokasi) { this.koordinatLokasi = koordinatLokasi; }
}