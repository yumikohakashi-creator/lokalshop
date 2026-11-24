package com.example.lokalshop.repository;

import com.example.lokalshop.model.SuperBoost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SuperBoostRepository extends JpaRepository<SuperBoost, Integer> {
    List<SuperBoost> findByStatusBoost(String statusBoost);
}