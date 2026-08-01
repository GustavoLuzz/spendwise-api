package com.gustavoluz.spendwise_api.repository;

import com.gustavoluz.spendwise_api.entity.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavingGoalRepository extends JpaRepository<SavingGoal, UUID> {
    List<SavingGoal> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<SavingGoal> findByIdAndUserId(UUID id, UUID userId);
}
