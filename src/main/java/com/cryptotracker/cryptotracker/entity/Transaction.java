package com.cryptotracker.cryptotracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;

    private Double amount; 
    private Double priceAtPurchase;
    private String type; 
    private LocalDateTime timestamp;
}