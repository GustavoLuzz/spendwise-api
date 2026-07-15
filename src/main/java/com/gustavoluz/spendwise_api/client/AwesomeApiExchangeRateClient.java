package com.gustavoluz.spendwise_api.client;

import com.gustavoluz.spendwise_api.client.dto.AwesomeApiQuoteDto;
import com.gustavoluz.spendwise_api.client.dto.AwesomeApiResponseDto;
import com.gustavoluz.spendwise_api.exception.ExchangeRateUnavailableException;
import com.gustavoluz.spendwise_api.model.ExchangeRateQuote;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Component
public class AwesomeApiExchangeRateClient implements ExchangeRateClient {

    private static final BigDecimal TWO = BigDecimal.valueOf(2);
    private final RestClient restClient;

    public AwesomeApiExchangeRateClient(@Qualifier("awesomeApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ExchangeRateQuote fetchUsdBrlQuote() {
        try {
            AwesomeApiResponseDto response = restClient.get()
                    .uri("/json/last/USD-BRL")
                    .retrieve()
                    .body(AwesomeApiResponseDto.class);

            return toQuote(response);
        } catch (RestClientException | IllegalArgumentException ex) {
            throw new ExchangeRateUnavailableException("Exchange rate service is temporarily unavailable", ex);
        }
    }

    private ExchangeRateQuote toQuote(AwesomeApiResponseDto response) {
        if (response == null || response.usdBrl() == null) {
            throw new IllegalArgumentException("Missing USD-BRL quote");
        }

        AwesomeApiQuoteDto quote = response.usdBrl();
        if (quote.bid() == null || quote.ask() == null || quote.timestamp() == null) {
            throw new IllegalArgumentException("Incomplete USD-BRL quote");
        }

        BigDecimal bid = new BigDecimal(quote.bid());
        BigDecimal ask = new BigDecimal(quote.ask());

        if (bid.signum() <= 0 || ask.signum() <= 0) {
            throw new IllegalArgumentException("Invalid USD-BRL quote");
        }

        BigDecimal referenceRate = bid.add(ask).divide(TWO, 8, RoundingMode.HALF_UP);
        Instant updatedAt = Instant.ofEpochSecond(Long.parseLong(quote.timestamp()));
        return new ExchangeRateQuote(referenceRate, updatedAt);
    }
}
