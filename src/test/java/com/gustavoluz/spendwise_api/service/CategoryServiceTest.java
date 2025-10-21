package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import com.gustavoluz.spendwise_api.exception.BadRequestException;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.repository.CategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private UserService userService;
    private CategoryService categoryService;
    private HttpServletRequest request;

    private final UUID categoryId = UUID.randomUUID();
    private final Category category = new Category();
    private final User user = new User();

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        userService = mock(UserService.class);
        categoryService = new CategoryService(categoryRepository, userService);
        request = mock(HttpServletRequest.class);

        user.setId(UUID.randomUUID());
        category.setId(categoryId);
        category.setName("Test Category");
        category.setType(CategoryType.EXPENSE);
        category.setUser(user);
        category.setIsGlobal(false);
    }

    @Test
    @DisplayName("Create should save category with user from request")
    void createShouldSaveCategoryWithUserFromRequest() {
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category created = categoryService.create(category, request);

        assertEquals(user, created.getUser());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Create global should save category with isGlobal true and no user")
    void createGlobalShouldSaveCategoryWithIsGlobalTrueAndNoUser() {
        category.setUser(user);
        category.setIsGlobal(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category created = categoryService.createGlobal(category);

        assertTrue(created.getIsGlobal());
        assertNull(created.getUser());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Find all should return global and user categories")
    void findAllShouldReturnGlobalAndUserCategories() {
        List<Category> global = List.of(new Category());
        List<Category> userCats = List.of(category);

        when(userService.getAuthenticated(request)).thenReturn(user);
        when(categoryRepository.findAllByIsGlobalTrue()).thenReturn(global);
        when(categoryRepository.findAllByUser(user)).thenReturn(userCats);

        List<Category> result = categoryService.findAll(request);

        assertTrue(result.containsAll(global));
        assertTrue(result.containsAll(userCats));
    }

    @Test
    @DisplayName("Find by id should return category when exists")
    void findByIdShouldReturnCategoryWhenExists() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category found = categoryService.findById(categoryId);

        assertEquals(category, found);
    }

    @Test
    @DisplayName("Find by id should throw when not found")
    void findByIdShouldThrowWhenNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(categoryId));
    }

    @Test
    @DisplayName("Update name should update and return category")
    void updateNameShouldUpdateAndReturnCategory() {
        String newName = "New Category";
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category updated = categoryService.updateName(categoryId, newName);

        assertEquals(newName, updated.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Update type should update and return category")
    void updateTypeShouldUpdateAndReturnCategory() {
        CategoryType newType = CategoryType.INCOME;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category updated = categoryService.updateType(categoryId, newType);

        assertEquals(newType, updated.getType());
        verify(categoryRepository).save(category);
    }

    @Test
    @DisplayName("Delete should delete when category is not global and exists")
    void deleteShouldDeleteWhenCategoryIsNotGlobalAndExists() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        categoryService.delete(categoryId);

        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("Delete should throw when category is global")
    void deleteShouldThrowWhenCategoryIsGlobal() {
        category.setIsGlobal(true);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        assertThrows(BadRequestException.class, () -> categoryService.delete(categoryId));
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Delete should throw when category not found")
    void deleteShouldThrowWhenCategoryNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(categoryId));
        verify(categoryRepository, never()).deleteById(any());
    }
}
