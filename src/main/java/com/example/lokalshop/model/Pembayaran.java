package com.example.lokalshop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pembayaran")
public class Pembayaran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pembayaran")
    private Integer idPembayaran;

    @OneToOne
    @JoinColumn(name = "id_transaksi", nullable = false, unique = true)
    private Transaksi transaksi;

    @Column(name = "metode_pembayaran")
    private String metodePembayaran;

    @Column(name = "nominal")
    private BigDecimal nominal;

    @Column(name = "status_pembayaran")
    private String statusPembayaran;

    @Column(name = "status_dana")
    private String statusDana;

    @Column(name = "waktu_bayar")
    private LocalDateTime waktuBayar;

    public Integer getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(Integer idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public Transaksi getTransaksi() {
        return transaksi;
    }

    public void setTransaksi(Transaksi transaksi) {
        this.transaksi = transaksi;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public BigDecimal getNominal() {
        return nominal;
    }

    public void setNominal(BigDecimal nominal) {
        this.nominal = nominal;
    }

    public String getStatusPembayaran() {
        return statusPembayaran;
    }

    public void setStatusPembayaran(String statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }

    public String getStatusDana() {
        return statusDana;
    }

    public void setStatusDana(String statusDana) {
        this.statusDana = statusDana;
    }

    public LocalDateTime getWaktuBayar() {
        return waktuBayar;
    }

    public void setWaktuBayar(LocalDateTime waktuBayar) {
        this.waktuBayar = waktuBayar;
    }
}