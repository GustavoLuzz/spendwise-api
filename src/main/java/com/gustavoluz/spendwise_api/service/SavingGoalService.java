package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.dto.savinggoal.SavingGoalUpdateDto;
import com.gustavoluz.spendwise_api.entity.SavingGoal;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.exception.BadRequestException;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.repository.SavingGoalRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavingGoalService {

    private final UserService userService;
    private final SavingGoalRepository repository;

    public SavingGoal create(SavingGoal goal, HttpServletRequest request) {
        goal.setName(normalizeName(goal.getName()));
        goal.setUser(userService.getAuthenticated(request));
        return repository.save(goal);
    }

    public List<SavingGoal> findAll(HttpServletRequest request) {
        User user = userService.getAuthenticated(request);
        return repository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public SavingGoal findById(UUID id, HttpServletRequest request) {
        User user = userService.getAuthenticated(request);
        return repository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Saving goal not found"));
    }

    public SavingGoal update(UUID id, SavingGoalUpdateDto dto, HttpServletRequest request) {
        SavingGoal goal = findById(id, request);

        if (dto.name() != null) {
            goal.setName(normalizeName(dto.name()));
        }
        if (dto.targetAmount() != null) {
            goal.setTargetAmount(dto.targetAmount());
        }
        if (dto.savedAmount() != null) {
            goal.setSavedAmount(dto.savedAmount());
        }
        goal.setTargetDate(dto.targetDate());

        return repository.save(goal);
    }

    public void delete(UUID id, HttpServletRequest request) {
        repository.delete(findById(id, request));
    }

    private String normalizeName(String name) {
        String normalized = name == null ? "" : name.trim();
        if (normalized.length() < 3 || normalized.length() > 80) {
            throw new BadRequestException("Name must be between 3 and 80 characters");
        }
        return normalized;
    }
}
