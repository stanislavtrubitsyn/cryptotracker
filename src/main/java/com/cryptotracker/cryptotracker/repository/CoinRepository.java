package com.cryptotracker.cryptotracker.repository;

import com.cryptotracker.cryptotracker.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
    Optional<Coin> findByPortfolioIdAndTicker(Long portfolioId, String ticker);
}