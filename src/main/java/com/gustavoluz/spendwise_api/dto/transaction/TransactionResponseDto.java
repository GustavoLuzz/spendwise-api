package com.gustavoluz.spendwise_api.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDto(
    UUID id,
    String description,
    BigDecimal amount,
    LocalDateTime createdAt,
    UUID categoryId
) {}
