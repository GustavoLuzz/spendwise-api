package com.gustavoluz.spendwise_api.model;

import com.gustavoluz.spendwise_api.exception.BadRequestException;

import java.util.Locale;

public enum CurrencyCode {
    USD,
    BRL;

    public static CurrencyCode from(String value) {
        if (value == null) {
            throw new BadRequestException("Currency is required");
        }

        try {
            return valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Supported currencies are USD and BRL");
        }
    }
}
