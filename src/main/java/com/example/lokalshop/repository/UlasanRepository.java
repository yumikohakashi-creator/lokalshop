package com.example.lokalshop.repository;

import com.example.lokalshop.model.Pengguna;
import com.example.lokalshop.model.Transaksi;
import com.example.lokalshop.model.Ulasan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UlasanRepository extends JpaRepository<Ulasan, Integer> {
    Optional<Ulasan> findByTransaksi(Transaksi transaksi);
    List<Ulasan> findByPenjual(Pengguna penjual);
}