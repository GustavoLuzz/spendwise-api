package com.gustavoluz.spendwise_api.repository;

import com.gustavoluz.spendwise_api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserId(UUID userId);
    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
}
