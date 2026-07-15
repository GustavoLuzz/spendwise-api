package com.gustavoluz.spendwise_api.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.gustavoluz.spendwise_api.client.ExchangeRateClient;
import com.gustavoluz.spendwise_api.model.ExchangeRateQuote;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ExchangeRateProviderCacheTest.TestConfig.class)
class ExchangeRateProviderCacheTest {

    @Autowired
    private ExchangeRateProvider provider;

    @Autowired
    private ExchangeRateClient client;

    @Test
    void shouldReuseCachedQuote() {
        ExchangeRateQuote quote = new ExchangeRateQuote(
                new BigDecimal("5.20"),
                Instant.parse("2026-07-15T15:00:00Z")
        );
        when(client.fetchUsdBrlQuote()).thenReturn(quote);

        ExchangeRateQuote first = provider.getUsdBrlQuote();
        ExchangeRateQuote second = provider.getUsdBrlQuote();

        assertSame(first, second);
        verify(client).fetchUsdBrlQuote();
    }

    @Configuration
    @EnableCaching
    static class TestConfig {

        @Bean
        ExchangeRateClient exchangeRateClient() {
            return mock(ExchangeRateClient.class);
        }

        @Bean
        ExchangeRateProvider exchangeRateProvider(ExchangeRateClient exchangeRateClient) {
            return new ExchangeRateProvider(exchangeRateClient);
        }

        @Bean
        CacheManager cacheManager() {
            CaffeineCacheManager manager = new CaffeineCacheManager("exchangeRates");
            manager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(1)));
            return manager;
        }
    }
}
