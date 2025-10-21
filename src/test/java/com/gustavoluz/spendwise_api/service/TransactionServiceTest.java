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

    private TransactionService service;
    private UserService userService;
    private CategoryService categoryService;
    private TransactionRepository repository;
    private HttpServletRequest request;
    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        categoryService = mock(CategoryService.class);
        repository = mock(TransactionRepository.class);
        request = mock(HttpServletRequest.class);
        service = new TransactionService(userService, categoryService, repository);

        user = new User();
        user.setId(UUID.randomUUID());

        category = new Category();
        category.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("Create should set user and category and save transaction")
    void createShouldSetUserAndCategoryAndSave() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.TEN);
        transaction.setDescription("Test");
        transaction.setCategory(category);

        when(userService.getAuthenticated(request)).thenReturn(user);
        when(categoryService.findById(category.getId())).thenReturn(category);
        when(repository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = service.create(transaction, request);

        assertEquals(user, result.getUser());
        assertEquals(category, result.getCategory());
        assertEquals(BigDecimal.TEN, result.getAmount());
        assertEquals("Test", result.getDescription());
    }

    @Test
    @DisplayName("FindAll should return transactions for authenticated user")
    void findAllShouldReturnUserTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        Transaction t = new Transaction();
        t.setId(UUID.randomUUID());
        t.setUser(user);
        transactions.add(t);

        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByUserId(user.getId())).thenReturn(transactions);

        List<Transaction> result = service.findAll(request);

        assertEquals(transactions, result);
    }

    @Test
    @DisplayName("FindById should return transaction for user")
    void findByIdShouldReturnTransaction() {
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setUser(user);

        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.of(transaction));

        Transaction result = service.findById(id, request);

        assertEquals(transaction, result);
    }

    @Test
    @DisplayName("FindById should throw if not found")
    void findByIdShouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();

        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(id, request));
    }

    @Test
    @DisplayName("Update should change fields and save")
    void updateShouldChangeFieldsAndSave() {
        UUID id = UUID.randomUUID();
        Transaction existing = new Transaction();
        existing.setId(id);
        existing.setUser(user);
        existing.setDescription("Old");
        existing.setAmount(BigDecimal.ONE);

        Transaction details = new Transaction();
        details.setDescription("New");
        details.setAmount(BigDecimal.TEN);
        details.setCategory(category);

        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.of(existing));
        when(categoryService.findById(category.getId())).thenReturn(category);
        when(repository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = service.update(id, details, request);

        assertEquals("New", result.getDescription());
        assertEquals(BigDecimal.TEN, result.getAmount());
        assertEquals(category, result.getCategory());
    }

    @Test
    @DisplayName("Delete should remove transaction")
    void deleteShouldRemoveTransaction() {
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setUser(user);

        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByIdAndUserId(id, user.getId())).thenReturn(Optional.of(transaction));

        service.delete(id, request);

        verify(repository).delete(transaction);
    }
}
