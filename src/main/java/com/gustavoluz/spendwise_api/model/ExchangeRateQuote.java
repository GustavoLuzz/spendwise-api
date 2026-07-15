package com.gustavoluz.spendwise_api.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeRateQuote(
        BigDecimal usdBrlRate,
        Instant updatedAt
) {
}
