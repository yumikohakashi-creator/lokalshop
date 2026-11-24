package com.example.lokalshop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "barang")
public class Barang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_barang")
    private Integer idBarang;

    @ManyToOne
    @JoinColumn(name = "id_penjual", nullable = false)
    private Pengguna penjual;

    @Column(name = "nama_barang")
    private String namaBarang;

    private String deskripsi;
    private BigDecimal harga;

    @Column(name = "kondisi_barang")
    private String kondisiBarang;

    @Column(name = "foto_barang")
    private String fotoBarang;

    @Column(name = "status_listing")
    private String statusListing = "Tersedia";

    // Atribut tambahan sementara untuk UI badge Super Boost
    @Transient
    private boolean isBoosted = false;

    // Getter & Setter
    public Integer getIdBarang() { return idBarang; }
    public void setIdBarang(Integer idBarang) { this.idBarang = idBarang; }
    public Pengguna getPenjual() { return penjual; }
    public void setPenjual(Pengguna penjual) { this.penjual = penjual; }
    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public BigDecimal getHarga() { return harga; }
    public void setHarga(BigDecimal harga) { this.harga = harga; }
    public String getKondisiBarang() { return kondisiBarang; }
    public void setKondisiBarang(String kondisiBarang) { this.kondisiBarang = kondisiBarang; }
    public String getFotoBarang() { return fotoBarang; }
    public void setFotoBarang(String fotoBarang) { this.fotoBarang = fotoBarang; }
    public String getStatusListing() { return statusListing; }
    public void setStatusListing(String statusListing) { this.statusListing = statusListing; }
    public boolean getIsBoosted() { return isBoosted; }
    public void setIsBoosted(boolean isBoosted) { this.isBoosted = isBoosted; }
}