package com.cryptotracker.cryptotracker.repository;

import com.cryptotracker.cryptotracker.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}