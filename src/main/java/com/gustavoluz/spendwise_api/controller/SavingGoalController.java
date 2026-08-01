package com.gustavoluz.spendwise_api.controller;

import com.gustavoluz.spendwise_api.dto.savinggoal.SavingGoalRequestDto;
import com.gustavoluz.spendwise_api.dto.savinggoal.SavingGoalResponseDto;
import com.gustavoluz.spendwise_api.dto.savinggoal.SavingGoalUpdateDto;
import com.gustavoluz.spendwise_api.entity.SavingGoal;
import com.gustavoluz.spendwise_api.mapper.SavingGoalMapper;
import com.gustavoluz.spendwise_api.service.SavingGoalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/saving-goals")
public class SavingGoalController {

    private final SavingGoalService service;
    private final SavingGoalMapper mapper;

    @PostMapping
    public ResponseEntity<SavingGoalResponseDto> create(
            @RequestBody @Valid SavingGoalRequestDto dto,
            HttpServletRequest request
    ) {
        SavingGoal created = service.create(mapper.toEntity(dto), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    @GetMapping
    public ResponseEntity<List<SavingGoalResponseDto>> findAll(HttpServletRequest request) {
        return ResponseEntity.ok(service.findAll(request).stream().map(mapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingGoalResponseDto> findById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(mapper.toDto(service.findById(id, request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SavingGoalResponseDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid SavingGoalUpdateDto dto,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(mapper.toDto(service.update(id, dto, request)));
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
