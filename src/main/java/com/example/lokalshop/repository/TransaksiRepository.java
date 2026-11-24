package com.example.lokalshop.repository;
import com.example.lokalshop.model.Transaksi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaksiRepository extends JpaRepository<Transaksi, Integer> {}