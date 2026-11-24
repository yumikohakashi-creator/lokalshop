package com.example.lokalshop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "super_boost")
public class SuperBoost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boost")
    private Integer idBoost;

    @ManyToOne
    @JoinColumn(name = "id_barang", nullable = false)
    private Barang barang;

    @Column(name = "durasi_jam")
    private Integer durasiJam;

    @Column(name = "harga_boost")
    private BigDecimal hargaBoost;

    @Column(name = "waktu_mulai")
    private LocalDateTime waktuMulai;

    @Column(name = "waktu_selesai")
    private LocalDateTime waktuSelesai;

    @Column(name = "status_boost")
    private String statusBoost; // 'Pending', 'Aktif', 'Selesai'

    // Getter & Setter
    public Integer getIdBoost() { return idBoost; }
    public void setIdBoost(Integer idBoost) { this.idBoost = idBoost; }
    public Barang getBarang() { return barang; }
    public void setBarang(Barang barang) { this.barang = barang; }
    public Integer getDurasiJam() { return durasiJam; }
    public void setDurasiJam(Integer durasiJam) { this.durasiJam = durasiJam; }
    public BigDecimal getHargaBoost() { return hargaBoost; }
    public void setHargaBoost(BigDecimal hargaBoost) { this.hargaBoost = hargaBoost; }
    public LocalDateTime getWaktuMulai() { return waktuMulai; }
    public void setWaktuMulai(LocalDateTime waktuMulai) { this.waktuMulai = waktuMulai; }
    public LocalDateTime getWaktuSelesai() { return waktuSelesai; }
    public void setWaktuSelesai(LocalDateTime waktuSelesai) { this.waktuSelesai = waktuSelesai; }
    public String getStatusBoost() { return statusBoost; }
    public void setStatusBoost(String statusBoost) { this.statusBoost = statusBoost; }
}