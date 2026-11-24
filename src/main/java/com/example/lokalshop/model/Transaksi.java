package com.example.lokalshop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaksi")
public class Transaksi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaksi")
    private Integer idTransaksi;

    @ManyToOne
    @JoinColumn(name = "id_barang", nullable = false)
    private Barang barang;

    @ManyToOne
    @JoinColumn(name = "id_pembeli", nullable = false)
    private Pengguna pembeli;

    @ManyToOne
    @JoinColumn(name = "id_cod", nullable = false)
    private CodPoint codPoint;

    @Column(name = "kode_pengajuan")
    private String kodePengajuan;

    @Column(name = "jumlah_barang")
    private Integer jumlahBarang;

    @Column(name = "total_harga")
    private BigDecimal totalHarga;

    @Column(name = "biaya_layanan")
    private BigDecimal biayaLayanan = BigDecimal.ZERO;

    @Column(name = "status_transaksi")
    private String statusTransaksi = "Pengajuan";

    @Column(name = "waktu_pertemuan")
    private LocalDateTime waktuPertemuan;

    @Column(name = "wkt_konfirm_penjual")
    private LocalDateTime wktKonfirmPenjual;

    @Column(name = "wkt_konfirm_pembeli")
    private LocalDateTime wktKonfirmPembeli;

    @Column(name = "alasan_batal")
    private String alasanBatal;

    // Getter & Setter
    public Integer getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(Integer idTransaksi) { this.idTransaksi = idTransaksi; }
    public Barang getBarang() { return barang; }
    public void setBarang(Barang barang) { this.barang = barang; }
    public Pengguna getPembeli() { return pembeli; }
    public void setPembeli(Pengguna pembeli) { this.pembeli = pembeli; }
    public CodPoint getCodPoint() { return codPoint; }
    public void setCodPoint(CodPoint codPoint) { this.codPoint = codPoint; }
    public String getKodePengajuan() { return kodePengajuan; }
    public void setKodePengajuan(String kodePengajuan) { this.kodePengajuan = kodePengajuan; }
    public Integer getJumlahBarang() { return jumlahBarang; }
    public void setJumlahBarang(Integer jumlahBarang) { this.jumlahBarang = jumlahBarang; }
    public BigDecimal getTotalHarga() { return totalHarga; }
    public void setTotalHarga(BigDecimal totalHarga) { this.totalHarga = totalHarga; }
    public BigDecimal getBiayaLayanan() { return biayaLayanan; }
    public void setBiayaLayanan(BigDecimal biayaLayanan) { this.biayaLayanan = biayaLayanan; }
    public String getStatusTransaksi() { return statusTransaksi; }
    public void setStatusTransaksi(String statusTransaksi) { this.statusTransaksi = statusTransaksi; }
    public LocalDateTime getWaktuPertemuan() { return waktuPertemuan; }
    public void setWaktuPertemuan(LocalDateTime waktuPertemuan) { this.waktuPertemuan = waktuPertemuan; }
    public LocalDateTime getWktKonfirmPenjual() { return wktKonfirmPenjual; }
    public void setWktKonfirmPenjual(LocalDateTime wktKonfirmPenjual) { this.wktKonfirmPenjual = wktKonfirmPenjual; }
    public LocalDateTime getWktKonfirmPembeli() { return wktKonfirmPembeli; }
    public void setWktKonfirmPembeli(LocalDateTime wktKonfirmPembeli) { this.wktKonfirmPembeli = wktKonfirmPembeli; }
    public String getAlasanBatal() { return alasanBatal; }
    public void setAlasanBatal(String alasanBatal) { this.alasanBatal = alasanBatal; }
}