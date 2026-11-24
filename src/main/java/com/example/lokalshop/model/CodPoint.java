package com.example.lokalshop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cod_point")
public class CodPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cod")
    private Integer idCod;

    @Column(name = "nama_titik")
    private String namaTitik;

    private String alamat;

    @Column(name = "koordinat_lokasi")
    private String koordinatLokasi;

    // Getter & Setter
    public Integer getIdCod() { return idCod; }
    public void setIdCod(Integer idCod) { this.idCod = idCod; }
    public String getNamaTitik() { return namaTitik; }
    public void setNamaTitik(String namaTitik) { this.namaTitik = namaTitik; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getKoordinatLokasi() { return koordinatLokasi; }
    public void setKoordinatLokasi(String koordinatLokasi) { this.koordinatLokasi = koordinatLokasi; }
}