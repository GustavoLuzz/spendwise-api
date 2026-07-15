package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.client.ExchangeRateClient;
import com.gustavoluz.spendwise_api.model.ExchangeRateQuote;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeRateProvider {

    private final ExchangeRateClient exchangeRateClient;

    @Cacheable(cacheNames = "exchangeRates", key = "'USD-BRL'", sync = true)
    public ExchangeRateQuote getUsdBrlQuote() {
        return exchangeRateClient.fetchUsdBrlQuote();
    }
}
