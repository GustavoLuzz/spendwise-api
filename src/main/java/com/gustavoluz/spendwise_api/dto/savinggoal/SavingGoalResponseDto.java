package com.gustavoluz.spendwise_api.dto.savinggoal;

import com.gustavoluz.spendwise_api.model.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record SavingGoalResponseDto(
        UUID id,
        String name,
        BigDecimal targetAmount,
        BigDecimal savedAmount,
        CurrencyCode currency,
        LocalDate targetDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
