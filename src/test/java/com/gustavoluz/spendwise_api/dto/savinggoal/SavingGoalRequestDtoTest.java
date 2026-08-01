package com.gustavoluz.spendwise_api.dto.savinggoal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavoluz.spendwise_api.model.CurrencyCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SavingGoalRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void rejectsInvalidName() {
        assertHasMessage(dto("ab", "100.00", "0.00", CurrencyCode.BRL),
                "Name must be between 3 and 80 characters");
    }

    @Test
    void rejectsInvalidTargetAmount() {
        assertHasMessage(dto("Emergency fund", "0.00", "0.00", CurrencyCode.BRL),
                "Target amount must be greater than zero");
        assertHasMessage(dto("Emergency fund", "10000000.01", "0.00", CurrencyCode.BRL),
                "Target amount cannot exceed 10000000");
    }

    @Test
    void rejectsInvalidSavedAmount() {
        assertHasMessage(dto("Emergency fund", "100.00", "-0.01", CurrencyCode.BRL),
                "Saved amount cannot be negative");
        assertHasMessage(dto("Emergency fund", "100.00", "10000000.01", CurrencyCode.BRL),
                "Saved amount cannot exceed 10000000");
    }

    @Test
    void rejectsMissingCurrency() {
        assertHasMessage(dto("Emergency fund", "100.00", "0.00", null),
                "Currency is required");
    }

    @Test
    void rejectsUnsupportedCurrency() {
        ObjectMapper objectMapper = new ObjectMapper();

        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue("""
                {
                  "name": "Emergency fund",
                  "targetAmount": 100.00,
                  "savedAmount": 0.00,
                  "currency": "EUR"
                }
                """, SavingGoalRequestDto.class));
    }

    @Test
    void acceptsSavedAmountAboveTarget() {
        Set<ConstraintViolation<SavingGoalRequestDto>> violations = validator.validate(
                dto("Emergency fund", "100.00", "150.00", CurrencyCode.USD)
        );

        assertTrue(violations.isEmpty());
    }

    private SavingGoalRequestDto dto(String name, String target, String saved, CurrencyCode currency) {
        return new SavingGoalRequestDto(
                name,
                new BigDecimal(target),
                new BigDecimal(saved),
                currency,
                null
        );
    }

    private void assertHasMessage(SavingGoalRequestDto dto, String message) {
        Set<ConstraintViolation<SavingGoalRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(violation -> message.equals(violation.getMessage())));
    }
}
