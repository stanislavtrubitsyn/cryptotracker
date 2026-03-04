package com.cryptotracker.cryptotracker.controller;

import com.cryptotracker.cryptotracker.entity.Portfolio;
import com.cryptotracker.cryptotracker.service.CryptoService;
import com.cryptotracker.cryptotracker.repository.PortfolioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller 
public class MainController {
    
    private final CryptoService cryptoService;
    private final PortfolioRepository portfolioRepository;

    public MainController(CryptoService cryptoService, PortfolioRepository portfolioRepository) {
        this.cryptoService = cryptoService;
        this.portfolioRepository = portfolioRepository;
    }

    @GetMapping("/") 
    public String index(Model model) {
        model.addAttribute("btcPrice", cryptoService.getBtcPrice());
        model.addAttribute("portfolios", portfolioRepository.findAll());
        return "index"; 
    }

    // Метод для створення портфеля
    @PostMapping("/add")
    public String addPortfolio(@RequestParam String name) {
        Portfolio portfolio = new Portfolio();
        portfolio.setName(name);
        portfolioRepository.save(portfolio);
        return "redirect:/"; // Перенаправляет на головну після збереження
    }

    // Метод для видалення портфеля
    @GetMapping("/delete/{id}")
    public String deletePortfolio(@PathVariable Long id) {
        portfolioRepository.deleteById(id);
        return "redirect:/";
    }
}