package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.exception.BadRequestException;
import com.gustavoluz.spendwise_api.exception.ExchangeRateUnavailableException;
import com.gustavoluz.spendwise_api.model.CurrencyCode;
import com.gustavoluz.spendwise_api.model.CurrencyConversionResult;
import com.gustavoluz.spendwise_api.model.ExchangeRateQuote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyConversionServiceTest {

    private static final Instant UPDATED_AT = Instant.parse("2026-07-15T15:00:00Z");
    private ExchangeRateProvider exchangeRateProvider;
    private CurrencyConversionService service;

    @BeforeEach
    void setUp() {
        exchangeRateProvider = mock(ExchangeRateProvider.class);
        service = new CurrencyConversionService(exchangeRateProvider);
        when(exchangeRateProvider.getUsdBrlQuote())
                .thenReturn(new ExchangeRateQuote(new BigDecimal("5.20000000"), UPDATED_AT));
    }

    @Test
    void shouldConvertUsdToBrl() {
        CurrencyConversionResult result = service.convert("USD", "BRL", new BigDecimal("100.00"));

        assertEquals(CurrencyCode.USD, result.from());
        assertEquals(CurrencyCode.BRL, result.to());
        assertEquals(new BigDecimal("5.20000000"), result.rate());
        assertEquals(new BigDecimal("520.00"), result.convertedAmount());
        assertEquals(UPDATED_AT, result.updatedAt());
    }

    @Test
    void shouldConvertBrlToUsdUsingInverseRate() {
        CurrencyConversionResult result = service.convert("BRL", "USD", new BigDecimal("520.00"));

        assertEquals(new BigDecimal("0.19230769"), result.rate());
        assertEquals(new BigDecimal("100.00"), result.convertedAmount());
    }

    @Test
    void shouldRejectUnsupportedCurrency() {
        assertThrows(
                BadRequestException.class,
                () -> service.convert("EUR", "BRL", BigDecimal.TEN)
        );
    }

    @Test
    void shouldRejectSameCurrency() {
        assertThrows(
                BadRequestException.class,
                () -> service.convert("USD", "USD", BigDecimal.TEN)
        );
    }

    @Test
    void shouldRejectInvalidAmounts() {
        assertThrows(BadRequestException.class, () -> service.convert("USD", "BRL", BigDecimal.ZERO));
        assertThrows(BadRequestException.class, () -> service.convert("USD", "BRL", new BigDecimal("1.001")));
        assertThrows(BadRequestException.class, () -> service.convert("USD", "BRL", new BigDecimal("10000000.01")));
    }

    @Test
    void shouldPropagateExternalServiceFailure() {
        when(exchangeRateProvider.getUsdBrlQuote())
                .thenThrow(new ExchangeRateUnavailableException("Exchange rate service is temporarily unavailable", null));

        assertThrows(
                ExchangeRateUnavailableException.class,
                () -> service.convert("USD", "BRL", BigDecimal.TEN)
        );
    }
}
