package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.entity.Transaction;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionRepository repository;

    public Transaction create(Transaction transaction, HttpServletRequest request) {
        User user = userService.getAuthenticated(request);
        transaction.setUser(user);

        if (transaction.getCategory() != null) {
            Category category = categoryService.findById(transaction.getCategory().getId());
            transaction.setCategory(category);
        }

        return repository.save(transaction);
    }

    public List<Transaction> findAll(HttpServletRequest request) {
        User user = userService.getAuthenticated(request);
        return repository.findByUserId(user.getId());
    }

    public Transaction findById(UUID id, HttpServletRequest request) {
        User user = userService.getAuthenticated(request);
        return repository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    public Transaction update(UUID id, Transaction transactionDetails, HttpServletRequest request) {
        Transaction transaction = findById(id, request);

        if (transactionDetails.getDescription() != null) {
            transaction.setDescription(transactionDetails.getDescription());
        }
        if (transactionDetails.getAmount() != null) {
            transaction.setAmount(transactionDetails.getAmount());
        }
        if (transactionDetails.getCategory() != null) {
            Category category = categoryService.findById(transactionDetails.getCategory().getId());
            transaction.setCategory(category);
        }

        return repository.save(transaction);
    }

    public void delete(UUID id, HttpServletRequest request) {
        Transaction transaction = findById(id, request);
        repository.delete(transaction);
    }
}