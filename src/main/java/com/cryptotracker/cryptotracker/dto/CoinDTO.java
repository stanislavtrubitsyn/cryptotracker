package com.cryptotracker.cryptotracker.dto;

import lombok.Data;

@Data
public class CoinDTO {
    private Long id;
    private String ticker;
    private Double amount;
    private Double currentPrice;
    private Double totalValue;
}