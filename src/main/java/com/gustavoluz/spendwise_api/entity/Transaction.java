package com.gustavoluz.spendwise_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal amount;
    private String description;
    private LocalDateTime date;

    @ManyToOne
    private Category category;

    @ManyToOne
    private User user;
}
