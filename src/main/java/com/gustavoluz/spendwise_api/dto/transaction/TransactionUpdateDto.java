package com.gustavoluz.spendwise_api.dto.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionUpdateDto(String description, BigDecimal amount, UUID categoryId) {
}
