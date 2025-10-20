package com.gustavoluz.spendwise_api.service;

import com.gustavoluz.spendwise_api.dto.user.DetailedUserDto;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> optUser = repository.findByEmail(username);

        if (optUser.isEmpty()) {

            throw new UsernameNotFoundException(String.format("User: %s not found", username));
        }

        User user = optUser.get();

        return new DetailedUserDto(user.getId(),user.getName(), user.getEmail(), user.getPassword(), user.getRole());
    }
}