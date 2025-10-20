package com.gustavoluz.spendwise_api.dto.user;

import com.gustavoluz.spendwise_api.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(UUID id, String name, String email, UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
