package com.gustavoluz.spendwise_api.dto.exchange;

import com.gustavoluz.spendwise_api.model.CurrencyCode;
import com.gustavoluz.spendwise_api.model.CurrencyConversionResult;

import java.math.BigDecimal;
import java.time.Instant;

public record CurrencyConversionResponseDto(
        CurrencyCode from,
        CurrencyCode to,
        BigDecimal amount,
        BigDecimal rate,
        BigDecimal convertedAmount,
        Instant updatedAt,
        String source
) {
    public static CurrencyConversionResponseDto from(CurrencyConversionResult result) {
        return new CurrencyConversionResponseDto(
                result.from(),
                result.to(),
                result.amount(),
                result.rate(),
                result.convertedAmount(),
                result.updatedAt(),
                "AwesomeAPI"
        );
    }
}
