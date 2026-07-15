package com.gustavoluz.spendwise_api.config.exchange;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class ExchangeRateCacheConfig {

    @Bean
    public CacheManager cacheManager(
            @Value("${app.exchange-rate.cache-ttl-seconds}") long cacheTtlSeconds
    ) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("exchangeRates");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(cacheTtlSeconds))
                .maximumSize(10)
                .recordStats());
        return cacheManager;
    }
}
