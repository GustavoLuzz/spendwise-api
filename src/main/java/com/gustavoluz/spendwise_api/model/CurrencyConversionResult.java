package com.gustavoluz.spendwise_api.model;

import java.math.BigDecimal;
import java.time.Instant;

public record CurrencyConversionResult(
        CurrencyCode from,
        CurrencyCode to,
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal convertedAmount,
        Instant updatedAt
) {
}
