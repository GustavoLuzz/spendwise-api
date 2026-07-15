package com.gustavoluz.spendwise_api.controller;

import com.gustavoluz.spendwise_api.dto.exchange.CurrencyConversionResponseDto;
import com.gustavoluz.spendwise_api.model.CurrencyConversionResult;
import com.gustavoluz.spendwise_api.service.CurrencyConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange-rates")
public class ExchangeRateController {

    private final CurrencyConversionService currencyConversionService;

    @GetMapping("/convert")
    public ResponseEntity<CurrencyConversionResponseDto> convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount
    ) {
        CurrencyConversionResult result = currencyConversionService.convert(from, to, amount);
        return ResponseEntity.ok(CurrencyConversionResponseDto.from(result));
    }
}
