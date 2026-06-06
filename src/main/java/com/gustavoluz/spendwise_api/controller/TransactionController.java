package com.gustavoluz.spendwise_api.controller;

import com.gustavoluz.spendwise_api.dto.transaction.TransactionRequestDto;
import com.gustavoluz.spendwise_api.dto.transaction.TransactionResponseDto;
import com.gustavoluz.spendwise_api.dto.transaction.TransactionUpdateDto;
import com.gustavoluz.spendwise_api.entity.Transaction;
import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import com.gustavoluz.spendwise_api.mapper.TransactionMapper;
import com.gustavoluz.spendwise_api.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

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
        Transaction created = service.create(transaction, dto.categoryId(), request);
        return ResponseEntity.ok(mapper.toDto(created));
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDto>> findAll(
            HttpServletRequest request,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) CategoryType categoryType,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Transaction> transactions = service.findAll(request, period, categoryId, categoryType, search, pageable);
        return ResponseEntity.ok(transactions.map(mapper::toDto));
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
            @RequestBody @Valid TransactionUpdateDto dto,
            HttpServletRequest request
    ) {
        Transaction transaction = mapper.toEntity(dto);
        Transaction updated = service.update(id, transaction, request);
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
