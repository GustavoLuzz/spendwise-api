package com.gustavoluz.spendwise_api.controller;

import com.gustavoluz.spendwise_api.dto.category.CategoryRequestDto;
import com.gustavoluz.spendwise_api.dto.category.CategoryResponseDto;
import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import com.gustavoluz.spendwise_api.mapper.CategoryMapper;
import com.gustavoluz.spendwise_api.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(
            @RequestBody @Valid CategoryRequestDto dto,
            HttpServletRequest request
    ) {

        Category category = categoryMapper.toEntity(dto);
        return ResponseEntity.ok(categoryMapper.toDto(service.create(category, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/global")
    public Category createGlobal(@RequestBody Category category) {
        return service.createGlobal(category);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> findAll(HttpServletRequest request) {

        List<Category> categories = service.findAll(request);

        if(categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.
                ok(categories
                .stream()
                .map(categoryMapper::toDto)
                .toList());

    }

    @GetMapping("/type")
    public ResponseEntity<List<CategoryResponseDto>> findAllByType(
            HttpServletRequest request, @RequestParam CategoryType type) {

        List<Category> categories = service.findAllByType(request, type);

        if(categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.
                ok(categories
                        .stream()
                        .map(categoryMapper::toDto)
                        .toList());
    }

    @GetMapping("/user")
    public ResponseEntity<List<CategoryResponseDto>> findAllByUser(HttpServletRequest request) {

        List<Category> categories = service.findAllByUser(request);

        if(categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.
                ok(categories
                        .stream()
                        .map(categoryMapper::toDto)
                        .toList());

    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable UUID id) {

        return ResponseEntity
                .ok(categoryMapper
                        .toDto(service
                                .findById(id)));

    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<CategoryResponseDto> updateName(@PathVariable UUID id, @RequestParam String name) {
        Category updated = service.updateName(id, name);
        return ResponseEntity.ok(categoryMapper.toDto(updated));
    }

    @PatchMapping("/{id}/type")
    public ResponseEntity<CategoryResponseDto> updateType(@PathVariable UUID id, @RequestParam CategoryType type) {
        Category updated = service.updateType(id, type);
        return ResponseEntity.ok(categoryMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){

        service.delete(id);
        return ResponseEntity.noContent().build();

    }



}
