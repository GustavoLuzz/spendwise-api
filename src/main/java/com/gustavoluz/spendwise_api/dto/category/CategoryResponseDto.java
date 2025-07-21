package com.gustavoluz.spendwise_api.dto.category;

import com.gustavoluz.spendwise_api.entity.enums.CategoryType;

import java.util.UUID;

public record CategoryResponseDto(UUID id, String name, CategoryType type) {
}
