package com.gustavoluz.spendwise_api.controller;

import com.gustavoluz.spendwise_api.dto.transaction.TransactionRequestDto;
import com.gustavoluz.spendwise_api.dto.transaction.TransactionResponseDto;
import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.entity.Transaction;
import com.gustavoluz.spendwise_api.mapper.TransactionMapper;
import com.gustavoluz.spendwise_api.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;
    private final TransactionMapper mapper;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(
            @RequestBody @Valid TransactionRequestDto dto,
            HttpServletRequest request
    ) {
        Transaction transaction = mapper.toEntity(dto);
        if (dto.getCategoryId() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            transaction.setCategory(category);
        }
        Transaction created = service.create(transaction, request);
        return ResponseEntity.ok(mapper.toDto(created));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> findAll(HttpServletRequest request) {
        List<Transaction> transactions = service.findAll(request);
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<TransactionResponseDto> dtos = transactions.stream()
                .map(mapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> findById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        Transaction transaction = service.findById(id, request);
        return ResponseEntity.ok(mapper.toDto(transaction));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> update(
            @PathVariable UUID id,
            @RequestBody TransactionRequestDto dto,
            HttpServletRequest request
    ) {
        Transaction transactionDetails = mapper.toEntity(dto);
        Transaction updated = service.update(id, transactionDetails, request);
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        service.delete(id, request);
        return ResponseEntity.noContent().build();
    }
}
