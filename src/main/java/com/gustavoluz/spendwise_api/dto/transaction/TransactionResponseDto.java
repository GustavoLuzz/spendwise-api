package com.gustavoluz.spendwise_api.dto.transaction;

import com.gustavoluz.spendwise_api.model.CurrencyCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.gustavoluz.spendwise_api.entity.enums.CategoryType;

public record TransactionResponseDto(
    UUID id,
    String description,
    BigDecimal amount,
    CurrencyCode currency,
    LocalDateTime createdAt,
    LocalDate optionalDate,
    UUID categoryId,
    String categoryName,
    CategoryType categoryType
) {}
