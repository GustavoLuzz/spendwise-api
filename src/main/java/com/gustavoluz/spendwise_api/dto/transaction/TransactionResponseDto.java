package com.gustavoluz.spendwise_api.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class TransactionResponseDto {
    private UUID id;
    private String description;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private UUID categoryId;
}
