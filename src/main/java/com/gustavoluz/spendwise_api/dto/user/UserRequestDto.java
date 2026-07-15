package com.gustavoluz.spendwise_api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank(message = "Name is required")
        @Size(min = 2, message = "Name must have at least 2 characters")
        String name,

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email,


        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 32,
                message = "Password must be between 8 and 32 characters"
        )
        @Pattern(
                regexp = "^(?=.*\\p{L})(?=.*\\d)[^\\p{Cntrl}]+$",
                message = "Password must include at least one letter and one number. Special characters are allowed"
        )
        String password
){

}
