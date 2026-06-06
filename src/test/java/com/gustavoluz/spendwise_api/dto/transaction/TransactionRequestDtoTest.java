package com.gustavoluz.spendwise_api.dto.transaction;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldAcceptMaximumTransactionAmount() {
        TransactionRequestDto dto = createDto(new BigDecimal("10000000"));

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void shouldRejectAmountAboveMaximum() {
        TransactionRequestDto dto = createDto(new BigDecimal("10000000.01"));

        assertEquals(
                "Amount cannot exceed 10000000",
                validator.validate(dto).iterator().next().getMessage()
        );
    }

    @Test
    void shouldRejectMissingAmount() {
        TransactionRequestDto dto = createDto(null);

        assertEquals(
                "Amount is required",
                validator.validate(dto).iterator().next().getMessage()
        );
    }

    @Test
    void shouldRejectMoreThanTwoDecimalPlaces() {
        TransactionRequestDto dto = createDto(new BigDecimal("10.123"));

        assertEquals(
                "Amount must have at most 2 decimal places",
                validator.validate(dto).iterator().next().getMessage()
        );
    }

    private TransactionRequestDto createDto(BigDecimal amount) {
        return new TransactionRequestDto(
                "Test transaction",
                amount,
                UUID.randomUUID(),
                null
        );
    }
}
