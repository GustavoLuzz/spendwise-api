package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.config.JwtTokenManager;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.exception.ResourceAlreadyExistsException;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;
    private JwtTokenManager jwtTokenManager;
    private AuthenticationManager authenticationManager;

    private final UUID userId = UUID.randomUUID();
    private final User user = new User();

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtTokenManager = mock(JwtTokenManager.class);
        authenticationManager = mock(AuthenticationManager.class);
        userService = new UserService(userRepository, jwtTokenManager, authenticationManager);

        user.setId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("plainPassword");
    }

    @Test
    @DisplayName("Create should save user with encrypted password when email not registered")
    void createShouldSaveUserWithEncryptedPasswordWhenEmailNotRegistered() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.create(user);

        assertNotEquals("plainPassword", created.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("plainPassword", created.getPassword()));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Create should throw when email already registered")
    void createShouldThrowWhenEmailAlreadyRegistered() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.create(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Find all should return user list")
    void findAllShouldReturnUserList() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(users, result);
    }

    @Test
    @DisplayName("Find by id should return user when exists")
    void findByIdShouldReturnUserWhenExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User found = userService.findById(userId);

        assertEquals(user, found);
    }

    @Test
    @DisplayName("Find by id should throw when not found")
    void findByIdShouldThrowWhenNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    @DisplayName("Update name should update and return user")
    void updateNameShouldUpdateAndReturnUser() {
        String newName = "New Name";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateName(userId, newName);

        assertEquals(newName, updated.getName());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Update email should update and return user when email not used")
    void updateEmailShouldUpdateAndReturnUserWhenEmailNotUsed() {
        String newEmail = "new@example.com";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(newEmail, userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updateEmail(userId, newEmail);

        assertEquals(newEmail, updated.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Update email should throw when email already used")
    void updateEmailShouldThrowWhenEmailAlreadyUsed() {
        String newEmail = "used@example.com";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(newEmail, userId)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateEmail(userId, newEmail));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete should delete when user exists")
    void deleteShouldDeleteWhenUserExists() {
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Delete should throw when user not found")
    void deleteShouldThrowWhenUserNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(userId));
        verify(userRepository, never()).deleteById(any());
    }
}