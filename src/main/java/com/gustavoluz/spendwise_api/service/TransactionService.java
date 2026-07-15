package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.entity.Transaction;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.entity.Category;
import com.gustavoluz.spendwise_api.entity.enums.CategoryType;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionRepository repository;

    public Transaction create(Transaction transaction, UUID categoryId, HttpServletRequest request) {
        User user = userService.getAuthenticated(request);
        transaction.setUser(user);

        Category category = categoryService.findById(categoryId, request);
        transaction.setCategory(category);

        if (transaction.getOptionalDate() == null) {
            transaction.setOptionalDate(LocalDate.now());
        }

        return repository.save(transaction);
    }

    public List<Transaction> findAll(HttpServletRequest request) {
        User user = userService.getAuthenticated(request);
        return repository.findByUserId(user.getId());
    }

    public Page<Transaction> findAll(
            HttpServletRequest request,
            String period,
            UUID categoryId,
            CategoryType categoryType,
            String search,
            Pageable pageable
    ) {
        User user = userService.getAuthenticated(request);

        Specification<Transaction> specification = Specification.where(hasUser(user.getId()))
                .and(hasCategoryId(categoryId))
                .and(hasCategoryType(categoryType))
                .and(descriptionContains(search))
                .and(createdInPeriod(period));

        return repository.findAll(specification, pageable);
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
        if (transactionDetails.getCurrency() != null) {
            transaction.setCurrency(transactionDetails.getCurrency());
        }
        if (transactionDetails.getCategory() != null) {
            Category category = categoryService.findById(transactionDetails.getCategory().getId(), request);
            transaction.setCategory(category);
        }

        return repository.save(transaction);
    }

    public void delete(UUID id, HttpServletRequest request) {
        Transaction transaction = findById(id, request);
        repository.delete(transaction);
    }

    private Specification<Transaction> hasUser(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    private Specification<Transaction> hasCategoryId(UUID categoryId) {
        if (categoryId == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<Transaction> hasCategoryType(CategoryType categoryType) {
        if (categoryType == null) {
            return null;
        }

        return (root, query, cb) -> cb.equal(root.join("category").get("type"), categoryType);
    }

    private Specification<Transaction> descriptionContains(String search) {
        if (search == null || search.trim().isEmpty()) {
            return null;
        }

        String term = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("description")), term);
    }

    private Specification<Transaction> createdInPeriod(String period) {
        if (period == null || period.trim().isEmpty() || period.equalsIgnoreCase("all")) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate;

        switch (period.toLowerCase(Locale.ROOT)) {
            case "week" -> startDate = today.minusDays(6);
            case "month" -> startDate = LocalDate.of(today.getYear(), today.getMonth(), 1);
            case "year" -> startDate = LocalDate.of(today.getYear(), 1, 1);
            default -> {
                return null;
            }
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        return (root, query, cb) -> cb.between(root.get("createdAt"), start, end);
    }
}
