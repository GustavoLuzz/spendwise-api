package com.gustavoluz.spendwise_api.config.exchange;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class AwesomeApiConfig {

    @Bean
    public RestClient awesomeApiRestClient(
            RestClient.Builder builder,
            @Value("${app.exchange-rate.base-url}") String baseUrl,
            @Value("${app.exchange-rate.api-key:}") String apiKey,
            @Value("${app.exchange-rate.connect-timeout-ms}") long connectTimeoutMs,
            @Value("${app.exchange-rate.read-timeout-ms}") long readTimeoutMs
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        RestClient.Builder configuredBuilder = builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory);

        if (StringUtils.hasText(apiKey)) {
            configuredBuilder.defaultHeader("x-api-key", apiKey);
        }

        return configuredBuilder.build();
    }
}
