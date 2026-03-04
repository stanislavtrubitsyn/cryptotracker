package com.cryptotracker.cryptotracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class PortfolioDTO {
    private Long id;
    private String name;
    private List<CoinDTO> coins;
    private Double totalValue;
}