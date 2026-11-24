package com.example.lokalshop.repository;

import com.example.lokalshop.model.Pembayaran;
import com.example.lokalshop.model.Transaksi;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PembayaranRepository extends JpaRepository<Pembayaran, Integer> {
   Optional<Pembayaran> findByTransaksi(Transaksi transaksi);
}