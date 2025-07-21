package com.gustavoluz.spendwise_api.dto.category;

import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(
        @NotBlank(message = "Name is required")
        @Size(min = 3, message = "Name must have at least 3 characters")
        String name,

        @NotNull(message = "A Type is required")
        CategoryType type) {
}
