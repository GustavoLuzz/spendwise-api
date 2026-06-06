package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import com.gustavoluz.spendwise_api.exception.BadRequestException;
import com.gustavoluz.spendwise_api.exception.ResourceAlreadyExistsException;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.repository.CategoryRepository;
import com.gustavoluz.spendwise_api.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public Category create(Category category, HttpServletRequest request) {

        User user = userService.getAuthenticated(request);
        ensureCategoryNameAvailable(category.getName(), user, false, null);
        category.setUser(user);

        return repository.save(category);
    }

    public Category createGlobal(Category category) {
        ensureCategoryNameAvailable(category.getName(), null, true, null);
        category.setUser(null);
        category.setIsGlobal(true);
        return repository.save(category);
    }

    public List<Category> findAll(HttpServletRequest request) {

        User user = userService.getAuthenticated(request);

        List<Category> globalCategories = repository.findAllByIsGlobalTrue();
        List<Category> userCategories = repository.findAllByUser(user);

        List<Category> categories = new java.util.ArrayList<>(globalCategories);
        categories.addAll(userCategories);

        return categories;
    }

    public List<Category> findAllByUser(HttpServletRequest request) {

        User user = userService.getAuthenticated(request);

        return repository.findAllByUser(user);
    }

    public List<Category> findAllByType(HttpServletRequest request, CategoryType type) {

        User user = userService.getAuthenticated(request);

        List<Category> categories = new java.util.ArrayList<>(
                repository.findAllByIsGlobalTrueAndType(type)
        );
        categories.addAll(repository.findAllByUserAndType(user, type));
        return categories;
    }

    public Category findById(UUID id, HttpServletRequest request) {
        User user = userService.getAuthenticated(request);
        Category category = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Category with id " + id + " not found"));

        if (!Boolean.TRUE.equals(category.getIsGlobal())
                && (category.getUser() == null || !category.getUser().getId().equals(user.getId()))) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }

        return category;
    }

    public Category updateName(UUID id, String name, HttpServletRequest request) {

        Category category = findOwnedCategory(id, request);
        String normalizedName = name == null ? "" : name.trim();
        if (normalizedName.length() < 3) {
            throw new BadRequestException("Name must have at least 3 characters");
        }
        ensureCategoryNameAvailable(normalizedName, category.getUser(), false, id);

        category.setName(normalizedName);
        return repository.save(category);
    }

    public Category updateType(UUID id, CategoryType type, HttpServletRequest request) {

        Category category = findOwnedCategory(id, request);

        category.setType(type);
        return repository.save(category);

    }

    public void delete(UUID id, HttpServletRequest request) {
        Category category = findOwnedCategory(id, request);

        if (transactionRepository.existsByCategoryId(id)) {
            throw new BadRequestException(
                    "Cannot delete a category with linked transactions"
            );
        }

        repository.delete(category);

    }

    private Category findOwnedCategory(UUID id, HttpServletRequest request) {
        Category category = findById(id, request);

        if (Boolean.TRUE.equals(category.getIsGlobal())) {
            throw new BadRequestException("Default categories cannot be modified");
        }

        return category;
    }

    private void ensureCategoryNameAvailable(String name, User user, boolean global, UUID excludeId) {
        boolean exists;

        if (global) {
            exists = excludeId == null
                    ? repository.existsByNameIgnoreCaseAndIsGlobalTrue(name)
                    : repository.existsByNameIgnoreCaseAndIsGlobalTrueAndIdNot(name, excludeId);
        } else {
            exists = excludeId == null
                    ? repository.existsByNameIgnoreCaseAndUser(name, user)
                    : repository.existsByNameIgnoreCaseAndUserAndIdNot(name, user, excludeId);
        }

        if (exists) {
            throw new ResourceAlreadyExistsException("Category already registered");
        }
    }

}
