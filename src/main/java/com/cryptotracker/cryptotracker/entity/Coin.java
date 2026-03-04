package com.cryptotracker.cryptotracker.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coins")
@Data
public class Coin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;
}