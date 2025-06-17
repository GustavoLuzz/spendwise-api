package com.gustavoluz.spendwise_api.dto.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(UUID id, String name, String email, LocalDateTime createdAt) {
}
