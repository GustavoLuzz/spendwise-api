package com.gustavoluz.spendwise_api.dto.transaction;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class TransactionRequestDto {
    private String description;
    private BigDecimal amount;
    private UUID categoryId;
}
