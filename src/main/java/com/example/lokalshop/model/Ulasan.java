package com.example.lokalshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ulasan")
public class Ulasan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ulasan")
    private Integer idUlasan;

    @OneToOne
    @JoinColumn(name = "id_transaksi", nullable = false, unique = true)
    private Transaksi transaksi;

    @ManyToOne
    @JoinColumn(name = "id_pengulas", nullable = false)
    private Pengguna pengulas;

    @ManyToOne
    @JoinColumn(name = "id_penjual", nullable = false)
    private Pengguna penjual;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "komentar")
    private String komentar;

    @Column(name = "waktu_ulasan")
    private LocalDateTime waktuUlasan = LocalDateTime.now();

    public Integer getIdUlasan() { return idUlasan; }
    public void setIdUlasan(Integer idUlasan) { this.idUlasan = idUlasan; }
    public Transaksi getTransaksi() { return transaksi; }
    public void setTransaksi(Transaksi transaksi) { this.transaksi = transaksi; }
    public Pengguna getPengulas() { return pengulas; }
    public void setPengulas(Pengguna pengulas) { this.pengulas = pengulas; }
    public Pengguna getPenjual() { return penjual; }
    public void setPenjual(Pengguna penjual) { this.penjual = penjual; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getKomentar() { return komentar; }
    public void setKomentar(String komentar) { this.komentar = komentar; }
    public LocalDateTime getWaktuUlasan() { return waktuUlasan; }
    public void setWaktuUlasan(LocalDateTime waktuUlasan) { this.waktuUlasan = waktuUlasan; }
}