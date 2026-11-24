package com.example.lokalshop.repository;

import com.example.lokalshop.model.Barang;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BarangRepository extends JpaRepository<Barang, Integer> {
    List<Barang> findByStatusListing(String statusListing);
    List<Barang> findByStatusListingAndNamaBarangContainingIgnoreCase(String statusListing, String keyword);
    List<Barang> findByStatusListingAndKondisiBarang(String statusListing, String kondisiBarang);
}