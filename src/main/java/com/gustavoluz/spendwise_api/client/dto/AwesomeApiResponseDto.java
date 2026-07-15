package com.gustavoluz.spendwise_api.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AwesomeApiResponseDto(
        @JsonProperty("USDBRL") AwesomeApiQuoteDto usdBrl
) {
}
