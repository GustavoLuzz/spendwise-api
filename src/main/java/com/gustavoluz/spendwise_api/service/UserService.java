package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.config.security.JwtTokenManager;
import com.gustavoluz.spendwise_api.dto.user.DetailedUserDto;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.entity.enums.UserRole;
import com.gustavoluz.spendwise_api.exception.ResourceAlreadyExistsException;
import com.gustavoluz.spendwise_api.exception.ResourceNotFoundException;
import com.gustavoluz.spendwise_api.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final JwtTokenManager jwtTokenManager;
    private final AuthenticationManager authenticationManager;

    public User create(User user) {

        boolean isEmailRegistered = repository.existsByEmailIgnoreCase(user.getEmail());

        if(isEmailRegistered) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encryptedPassword);

        return repository.save(user);
    }

    public String authenticate(User user, HttpServletResponse response) {

        final UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword());

        final Authentication authentication = this.authenticationManager.authenticate(credentials);



        repository.findByEmailIgnoreCase(user.getEmail())
                .orElseThrow(
                        () -> new ResponseStatusException(404, "Email do usuário não cadastrado", null)
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenManager.generateToken(authentication);
    }

    public List<User> findAll() {
        ensureAdmin();
        return repository.findAll();
    }

    public User findById(UUID id) {
        ensureCanAccess(id);
        return findByIdInternal(id);
    }

    private User findByIdInternal(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id + " not found")
        );
    }

    public User updateName(UUID id, String name) {

        ensureCanAccess(id);
        User user = findByIdInternal(id);
        user.setName(name);

        return repository.save(user);
    }

    public User getAuthenticated(HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();

        return repository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateEmail(UUID id, String email) {

        ensureCanAccess(id);
        User user = findByIdInternal(id);

        if(repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        user.setEmail(email);
        return repository.save(user);
    }

    // need the password update method

    public void delete(UUID id) {

        ensureCanAccess(id);
        if(!repository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }

        repository.deleteById(id);

    }

    private void ensureAdmin() {
        if (getAuthenticatedPrincipal().role() != UserRole.ADMIN) {
            throw new AccessDeniedException("Administrator access required");
        }
    }

    private void ensureCanAccess(UUID userId) {
        DetailedUserDto principal = getAuthenticatedPrincipal();
        boolean isAdmin = principal.role() == UserRole.ADMIN;
        boolean isOwner = principal.id().equals(userId);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private DetailedUserDto getAuthenticatedPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof DetailedUserDto principal)) {
            throw new AccessDeniedException("Authentication required");
        }

        return principal;
    }
}
