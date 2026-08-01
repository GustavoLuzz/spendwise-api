package com.gustavoluz.spendwise_api.dto.savinggoal;

import com.gustavoluz.spendwise_api.model.CurrencyCode;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SavingGoalRequestDto(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
        String name,
        @NotNull(message = "Target amount is required")
        @Positive(message = "Target amount must be greater than zero")
        @DecimalMax(value = "10000000", message = "Target amount cannot exceed 10000000")
        @Digits(integer = 8, fraction = 2, message = "Target amount must have at most 2 decimal places")
        BigDecimal targetAmount,
        @NotNull(message = "Saved amount is required")
        @PositiveOrZero(message = "Saved amount cannot be negative")
        @DecimalMax(value = "10000000", message = "Saved amount cannot exceed 10000000")
        @Digits(integer = 8, fraction = 2, message = "Saved amount must have at most 2 decimal places")
        BigDecimal savedAmount,
        @NotNull(message = "Currency is required")
        CurrencyCode currency,
        LocalDate targetDate
) {
}
