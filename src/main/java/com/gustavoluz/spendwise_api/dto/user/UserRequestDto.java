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
        @Pattern(
                regexp = "^(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "Password must be at least 8 characters and contain at least one number"
        )
        String password
){

}
