package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import com.gustavoluz.spendwise_api.exception.BadRequestException;
import com.gustavoluz.spendwise_api.exception.ResourceAlreadyExistsException;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.repository.CategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
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

        return repository.findAllByType(type);
    }

    public Category findById(UUID id, HttpServletRequest request) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Category with id " + id + " not found"));
    }

    public Category updateName(UUID id, String name, HttpServletRequest request) {

        Category category = findById(id, request);
        ensureCategoryNameAvailable(name, category.getUser(), Boolean.TRUE.equals(category.getIsGlobal()), id);

        category.setName(name);
        return repository.save(category);
    }

    public Category updateType(UUID id, CategoryType type, HttpServletRequest request) {

        Category category = findById(id, request);

        category.setType(type);
        return repository.save(category);

    }

    public void delete(UUID id, HttpServletRequest request) {
        Category category = findById(id, request);

        if (Boolean.TRUE.equals(category.getIsGlobal())) {
            throw new BadRequestException("Cannot delete global category");
        }

        if(!repository.existsById(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }

        repository.deleteById(id);

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
