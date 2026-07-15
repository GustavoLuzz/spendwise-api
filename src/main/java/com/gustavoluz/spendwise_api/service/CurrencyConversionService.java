package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.exception.BadRequestException;
import com.gustavoluz.spendwise_api.model.CurrencyCode;
import com.gustavoluz.spendwise_api.model.CurrencyConversionResult;
import com.gustavoluz.spendwise_api.model.ExchangeRateQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000000");
    private final ExchangeRateProvider exchangeRateProvider;

    public CurrencyConversionResult convert(String fromValue, String toValue, BigDecimal amount) {
        CurrencyCode from = CurrencyCode.from(fromValue);
        CurrencyCode to = CurrencyCode.from(toValue);
        validate(from, to, amount);

        ExchangeRateQuote quote = exchangeRateProvider.getUsdBrlQuote();
        BigDecimal rate = from == CurrencyCode.USD
                ? quote.usdBrlRate()
                : BigDecimal.ONE.divide(quote.usdBrlRate(), 8, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        return new CurrencyConversionResult(
                from,
                to,
                amount.setScale(2, RoundingMode.HALF_UP),
                rate,
                convertedAmount,
                quote.updatedAt()
        );
    }

    private void validate(CurrencyCode from, CurrencyCode to, BigDecimal amount) {
        if (from == to) {
            throw new BadRequestException("Source and target currencies must be different");
        }
        if (amount == null) {
            throw new BadRequestException("Amount is required");
        }
        if (amount.signum() <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new BadRequestException("Amount cannot exceed 10000000");
        }
        if (amount.scale() > 2) {
            throw new BadRequestException("Amount must have at most 2 decimal places");
        }
    }
}
