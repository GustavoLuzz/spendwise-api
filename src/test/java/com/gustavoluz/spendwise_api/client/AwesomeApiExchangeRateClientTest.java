package com.gustavoluz.spendwise_api.client;

import com.gustavoluz.spendwise_api.exception.ExchangeRateUnavailableException;
import com.gustavoluz.spendwise_api.model.ExchangeRateQuote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AwesomeApiExchangeRateClientTest {

    private MockRestServiceServer server;
    private AwesomeApiExchangeRateClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://exchange.test");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new AwesomeApiExchangeRateClient(builder.build());
    }

    @Test
    void shouldReturnReferenceRateFromBidAndAsk() {
        server.expect(once(), requestTo("https://exchange.test/json/last/USD-BRL"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "USDBRL": {
                            "bid": "5.20",
                            "ask": "5.22",
                            "timestamp": "1710000000"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        ExchangeRateQuote quote = client.fetchUsdBrlQuote();

        assertEquals(new BigDecimal("5.21000000"), quote.usdBrlRate());
        assertEquals(Instant.ofEpochSecond(1710000000L), quote.updatedAt());
        server.verify();
    }

    @Test
    void shouldRejectInvalidExternalResponse() {
        server.expect(once(), requestTo("https://exchange.test/json/last/USD-BRL"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        assertThrows(ExchangeRateUnavailableException.class, client::fetchUsdBrlQuote);
        server.verify();
    }

    @Test
    void shouldHandleExternalServiceError() {
        server.expect(once(), requestTo("https://exchange.test/json/last/USD-BRL"))
                .andRespond(withServerError());

        assertThrows(ExchangeRateUnavailableException.class, client::fetchUsdBrlQuote);
        server.verify();
    }
}
