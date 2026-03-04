package com.cryptotracker.cryptotracker.controller;

import com.cryptotracker.cryptotracker.service.CryptoService;
import com.cryptotracker.cryptotracker.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final PortfolioService portfolioService;
    private final CryptoService cryptoService;

    @GetMapping("/")
    public String index(Model model, 
                        @RequestParam(defaultValue = "BTC,ETH,SOL") List<String> displayCoins,
                        @RequestParam(defaultValue = "BTC") String chartCoin,
                        @RequestParam(defaultValue = "7") int days) {
        
        Map<String, Double> allPrices = cryptoService.getAllPrices();
        
        Map<String, Double> topPrices = new LinkedHashMap<>();
        for (String ticker : displayCoins) {
            String upperTicker = ticker.toUpperCase();
            if (allPrices.containsKey(upperTicker)) {
                topPrices.put(upperTicker, allPrices.get(upperTicker));
            }
        }

        model.addAttribute("topPrices", topPrices);
        model.addAttribute("selectedDisplayCoins", displayCoins);
        model.addAttribute("btcPrice", allPrices.getOrDefault("BTC", 0.0));
        model.addAttribute("chartCoin", chartCoin.toUpperCase());
        model.addAttribute("chartDays", days);
        model.addAttribute("historyData", cryptoService.getCoinHistory(chartCoin, days));
        model.addAttribute("portfolios", portfolioService.getAllPortfoliosDTO(allPrices));
        model.addAttribute("availableTickers", allPrices.keySet());
        
        return "index";
    }

    @PostMapping("/add")
    public String addPortfolio(@RequestParam String name) {
        if (name != null && !name.isBlank()) {
            portfolioService.createPortfolio(name);
        }
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return "redirect:/";
    }

    @PostMapping("/edit/{id}")
    public String editPortfolio(@PathVariable Long id, @RequestParam String newName) {
        if (newName != null && !newName.isBlank()) {
            portfolioService.updatePortfolioName(id, newName);
        }
        return "redirect:/";
    }

    @PostMapping("/add-coin/{id}")
    public String addCoin(@PathVariable Long id, 
                          @RequestParam String ticker, 
                          @RequestParam Double amount) {
        if (amount != null && amount >= 0) {
            Double currentPrice = cryptoService.getAllPrices().getOrDefault(ticker.toUpperCase(), 0.0);
            portfolioService.addCoinToPortfolio(id, ticker.toUpperCase(), amount, currentPrice);
        }
        return "redirect:/";
    }

    @PostMapping("/edit-coin/{coinId}")
    public String editCoin(@PathVariable Long coinId, @RequestParam Double newAmount) {
        if (newAmount != null && newAmount >= 0) {
            String ticker = portfolioService.getCoinTickerById(coinId);
            Double currentPrice = cryptoService.getAllPrices().getOrDefault(ticker, 0.0);
            portfolioService.updateCoinAmount(coinId, newAmount, currentPrice);
        }
        return "redirect:/";
    }

    @GetMapping("/delete-coin/{coinId}")
    public String deleteCoin(@PathVariable Long coinId) {
        portfolioService.deleteCoin(coinId);
        return "redirect:/";
    }
}