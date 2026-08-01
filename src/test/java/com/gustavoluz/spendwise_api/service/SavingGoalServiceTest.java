package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.dto.savinggoal.SavingGoalUpdateDto;
import com.gustavoluz.spendwise_api.entity.SavingGoal;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.exception.BadRequestException;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.model.CurrencyCode;
import com.gustavoluz.spendwise_api.repository.SavingGoalRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SavingGoalServiceTest {

    private UserService userService;
    private SavingGoalRepository repository;
    private SavingGoalService service;
    private HttpServletRequest request;
    private User user;
    private SavingGoal goal;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        repository = mock(SavingGoalRepository.class);
        service = new SavingGoalService(userService, repository);
        request = mock(HttpServletRequest.class);

        user = new User();
        user.setId(UUID.randomUUID());

        goal = new SavingGoal();
        goal.setId(UUID.randomUUID());
        goal.setName("Emergency fund");
        goal.setTargetAmount(new BigDecimal("10000.00"));
        goal.setSavedAmount(new BigDecimal("2000.00"));
        goal.setCurrency(CurrencyCode.USD);
        goal.setUser(user);
    }

    @Test
    void createAssignsAuthenticatedUserAndNormalizesName() {
        goal.setName("  Emergency fund  ");
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.save(goal)).thenReturn(goal);

        SavingGoal result = service.create(goal, request);

        assertEquals("Emergency fund", result.getName());
        assertEquals(user, result.getUser());
        verify(repository).save(goal);
    }

    @Test
    void findAllOnlyUsesAuthenticatedUser() {
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findAllByUserIdOrderByCreatedAtDesc(user.getId())).thenReturn(List.of(goal));

        assertEquals(List.of(goal), service.findAll(request));
        verify(repository).findAllByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Test
    void findByIdUsesGoalAndAuthenticatedUserIds() {
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByIdAndUserId(goal.getId(), user.getId())).thenReturn(Optional.of(goal));

        assertEquals(goal, service.findById(goal.getId(), request));
    }

    @Test
    void findByIdHidesGoalsOwnedByAnotherUser() {
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByIdAndUserId(goal.getId(), user.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(goal.getId(), request));
    }

    @Test
    void updateChangesEditableFieldsButPreservesCurrency() {
        LocalDate targetDate = LocalDate.of(2027, 12, 31);
        SavingGoalUpdateDto update = new SavingGoalUpdateDto(
                "Travel fund",
                new BigDecimal("15000.00"),
                new BigDecimal("16000.00"),
                targetDate
        );
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByIdAndUserId(goal.getId(), user.getId())).thenReturn(Optional.of(goal));
        when(repository.save(goal)).thenReturn(goal);

        SavingGoal result = service.update(goal.getId(), update, request);

        assertEquals("Travel fund", result.getName());
        assertEquals(new BigDecimal("15000.00"), result.getTargetAmount());
        assertEquals(new BigDecimal("16000.00"), result.getSavedAmount());
        assertEquals(targetDate, result.getTargetDate());
        assertEquals(CurrencyCode.USD, result.getCurrency());
    }

    @Test
    void updateCanClearTargetDate() {
        goal.setTargetDate(LocalDate.now().plusYears(1));
        SavingGoalUpdateDto update = new SavingGoalUpdateDto(null, null, null, null);
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByIdAndUserId(goal.getId(), user.getId())).thenReturn(Optional.of(goal));
        when(repository.save(goal)).thenReturn(goal);

        SavingGoal result = service.update(goal.getId(), update, request);

        assertEquals(null, result.getTargetDate());
    }

    @Test
    void deleteUsesOwnedGoal() {
        when(userService.getAuthenticated(request)).thenReturn(user);
        when(repository.findByIdAndUserId(goal.getId(), user.getId())).thenReturn(Optional.of(goal));

        service.delete(goal.getId(), request);

        verify(repository).delete(goal);
    }

    @Test
    void rejectsNameThatOnlyPassesLengthBeforeTrimming() {
        goal.setName("  a  ");

        assertThrows(BadRequestException.class, () -> service.create(goal, request));
    }
}
