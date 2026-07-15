package com.gustavoluz.spendwise_api.client;

import com.gustavoluz.spendwise_api.model.ExchangeRateQuote;

public interface ExchangeRateClient {
    ExchangeRateQuote fetchUsdBrlQuote();
}
