package com.gustavoluz.spendwise_api.dto.user;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldAcceptPasswordWithSpecialCharacters() {
        UserRequestDto dto = createDto("Gustavo@123!");

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void shouldAcceptPasswordWithSpaces() {
        UserRequestDto dto = createDto("gustavo 123");

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void shouldRejectPasswordWithoutNumber() {
        UserRequestDto dto = createDto("Gustavo@abc");

        assertEquals(
                "Password must include at least one letter and one number. Special characters are allowed",
                validator.validate(dto).iterator().next().getMessage()
        );
    }

    @Test
    void shouldRejectShortPassword() {
        UserRequestDto dto = createDto("Gu1!");

        assertEquals(
                "Password must be between 8 and 72 characters",
                validator.validate(dto).iterator().next().getMessage()
        );
    }

    private UserRequestDto createDto(String password) {
        return new UserRequestDto(
                "Gustavo",
                "gustavo@email.com",
                password
        );
    }
}
