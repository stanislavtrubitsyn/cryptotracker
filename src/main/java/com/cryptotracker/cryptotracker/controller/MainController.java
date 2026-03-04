package com.cryptotracker.cryptotracker.controller;

import com.cryptotracker.cryptotracker.service.CryptoService;
import com.cryptotracker.cryptotracker.repository.PortfolioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}