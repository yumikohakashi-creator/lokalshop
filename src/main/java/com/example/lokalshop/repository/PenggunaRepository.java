package com.example.lokalshop.repository;

import com.example.lokalshop.model.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PenggunaRepository extends JpaRepository<Pengguna, Integer> {
    Optional<Pengguna> findByEmail(String email);
}