package com.gustavoluz.spendwise_api.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactionRequestDto(
    @NotBlank String description,
    @Positive BigDecimal amount,
    @NotNull UUID categoryId,
    LocalDate optionalDate
) {}
