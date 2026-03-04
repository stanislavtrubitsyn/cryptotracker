package com.cryptotracker.cryptotracker.service;

import com.cryptotracker.cryptotracker.dto.CoinDTO;
import com.cryptotracker.cryptotracker.dto.PortfolioDTO;
import com.cryptotracker.cryptotracker.entity.Coin;
import com.cryptotracker.cryptotracker.entity.Portfolio;
import com.cryptotracker.cryptotracker.entity.Transaction;
import com.cryptotracker.cryptotracker.repository.CoinRepository;
import com.cryptotracker.cryptotracker.repository.PortfolioRepository;
import com.cryptotracker.cryptotracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final CoinRepository coinRepository;
    private final TransactionRepository transactionRepository;

    public List<PortfolioDTO> getAllPortfoliosDTO(Map<String, Double> prices) {
        return portfolioRepository.findAll().stream()
                .map(p -> convertToDTO(p, prices))
                .toList();
    }

    public void createPortfolio(String name) {
        Portfolio p = new Portfolio();
        p.setName(name);
        portfolioRepository.save(p);
    }

    public void deletePortfolio(Long id) {
        portfolioRepository.deleteById(id);
    }

    public void updatePortfolioName(Long id, String newName) {
        Portfolio p = portfolioRepository.findById(id).orElseThrow();
        p.setName(newName);
        portfolioRepository.save(p);
    }

    @Transactional
    public void addCoinToPortfolio(Long portfolioId, String ticker, Double amount, Double priceAtPurchase) {
        Portfolio p = portfolioRepository.findById(portfolioId).orElseThrow();
        
        Coin coin = coinRepository.findByPortfolioIdAndTicker(portfolioId, ticker.toUpperCase())
                .orElseGet(() -> {
                    Coin newCoin = new Coin();
                    newCoin.setTicker(ticker.toUpperCase());
                    newCoin.setAmount(0.0);
                    newCoin.setPortfolio(p);
                    return coinRepository.save(newCoin);
                });

        coin.setAmount(coin.getAmount() + amount);
        coinRepository.save(coin);

        Transaction t = new Transaction();
        t.setCoin(coin);
        t.setAmount(amount);
        t.setType("BUY");
        t.setTimestamp(LocalDateTime.now());
        t.setPriceAtPurchase(priceAtPurchase);
        transactionRepository.save(t);
    }

    @Transactional
    public void updateCoinAmount(Long coinId, Double newAmount, Double priceAtPurchase) {
        Coin coin = coinRepository.findById(coinId).orElseThrow();
        Double oldAmount = coin.getAmount();
        Double diff = newAmount - oldAmount;

        if (diff == 0) return;

        coin.setAmount(newAmount);
        coinRepository.save(coin);

        Transaction t = new Transaction();
        t.setCoin(coin);
        t.setAmount(Math.abs(diff));
        t.setType(diff > 0 ? "BUY_EDIT" : "SELL_EDIT");
        t.setTimestamp(LocalDateTime.now());
        t.setPriceAtPurchase(priceAtPurchase);
        transactionRepository.save(t);
    }

    public String getCoinTickerById(Long coinId) {
        return coinRepository.findById(coinId)
                .map(Coin::getTicker)
                .orElse("");
    }

    public void deleteCoin(Long coinId) {
        coinRepository.deleteById(coinId);
    }

    private PortfolioDTO convertToDTO(Portfolio p, Map<String, Double> prices) {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setId(p.getId()); 
        dto.setName(p.getName());

        List<CoinDTO> coinDTOs = p.getCoins().stream().map(coin -> {
            CoinDTO cDto = new CoinDTO();
            cDto.setId(coin.getId());
            cDto.setTicker(coin.getTicker());
            cDto.setAmount(coin.getAmount());
            Double price = prices.getOrDefault(coin.getTicker(), 0.0);
            cDto.setCurrentPrice(price);
            cDto.setTotalValue(coin.getAmount() * price);
            return cDto;
        }).toList();

        dto.setCoins(coinDTOs);
        dto.setTotalValue(coinDTOs.stream().mapToDouble(CoinDTO::getTotalValue).sum());
        return dto;
    }
}