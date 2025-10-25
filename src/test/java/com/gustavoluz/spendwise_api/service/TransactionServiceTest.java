package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.entity.Transaction;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionService transactionService;
    private UserService userService;
    private CategoryService categoryService;
    private TransactionRepository transactionRepository;
    private HttpServletRequest request;
    private User user;
    private Category category;
    private Transaction transaction;
    private final UUID transactionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        categoryService = mock(CategoryService.class);
        transactionRepository = mock(TransactionRepository.class);
        request = mock(HttpServletRequest.class);
        transactionService = new TransactionService(userService, categoryService, transactionRepository);

        user = new User();
        user.setId(UUID.randomUUID());

        category = new Category();
        category.setId(UUID.randomUUID());

        transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDescription("Test Transaction");
    }

    @Test
    @DisplayName("Create should set user and category and save transaction")
    void createShouldSetUserAndCategoryAndSaveTransaction() {
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(categoryService.findById(category.getId(), request)).thenReturn(category);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = transactionService.create(transaction, category.getId(), request);

        assertEquals(user, result.getUser());
        assertEquals(category, result.getCategory());
        assertEquals(BigDecimal.TEN, result.getAmount());
        assertEquals("Test Transaction", result.getDescription());
        verify(transactionRepository).save(transaction);
    }

    @Test
    @DisplayName("FindAll should return transactions for authenticated user")
    void findAllShouldReturnTransactionsForUser() {
        List<Transaction> transactions = List.of(transaction);
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(transactionRepository.findByUserId(user.getId())).thenReturn(transactions);

        List<Transaction> result = transactionService.findAll(request);

        assertEquals(transactions, result);
    }

    @Test
    @DisplayName("FindById should return transaction for user")
    void findByIdShouldReturnTransactionForUser() {
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(transactionRepository.findByIdAndUserId(transactionId, user.getId())).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.findById(transactionId, request);

        assertEquals(transaction, result);
    }

    @Test
    @DisplayName("FindById should throw if not found")
    void findByIdShouldThrowIfNotFound() {
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(transactionRepository.findByIdAndUserId(transactionId, user.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.findById(transactionId, request));
    }

    @Test
    @DisplayName("Update should change fields and save")
    void updateShouldChangeFieldsAndSave() {
        Transaction existing = new Transaction();
        existing.setId(transactionId);
        existing.setUser(user);
        existing.setDescription("Old");
        existing.setAmount(BigDecimal.ONE);
        existing.setCategory(category);

        Transaction details = new Transaction();
        details.setDescription("New Description");
        details.setAmount(BigDecimal.valueOf(99.99));
        Category newCategory = new Category();
        newCategory.setId(UUID.randomUUID());
        details.setCategory(newCategory);

        when(userService.getAuthenticated(request)).thenReturn(user);
        when(transactionRepository.findByIdAndUserId(transactionId, user.getId())).thenReturn(Optional.of(existing));
        when(categoryService.findById(newCategory.getId(), request)).thenReturn(newCategory);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = transactionService.update(transactionId, details, request);

        assertEquals("New Description", result.getDescription());
        assertEquals(BigDecimal.valueOf(99.99), result.getAmount());
        assertEquals(newCategory, result.getCategory());
        verify(transactionRepository).save(existing);
    }

    @Test
    @DisplayName("Delete should remove transaction")
    void deleteShouldRemoveTransaction() {
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(transactionRepository.findByIdAndUserId(transactionId, user.getId())).thenReturn(Optional.of(transaction));

        transactionService.delete(transactionId, request);

        verify(transactionRepository).delete(transaction);
    }
}

