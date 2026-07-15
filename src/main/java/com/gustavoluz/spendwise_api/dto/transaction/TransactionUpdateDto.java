package com.gustavoluz.spendwise_api.dto.transaction;

import com.gustavoluz.spendwise_api.model.CurrencyCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;

public record TransactionUpdateDto(
        String description,
        @Positive(message = "Amount must be greater than zero")
        @DecimalMax(value = "10000000", message = "Amount cannot exceed 10000000")
        @Digits(integer = 8, fraction = 2, message = "Amount must have at most 2 decimal places")
        BigDecimal amount,
        CurrencyCode currency,
        UUID categoryId,
        LocalDate optionalDate
) {
}
