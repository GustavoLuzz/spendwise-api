package com.gustavoluz.spendwise_api.controller;

import com.gustavoluz.spendwise_api.dto.user.UserLoginDto;
import com.gustavoluz.spendwise_api.dto.user.UserRequestDto;
import com.gustavoluz.spendwise_api.dto.user.UserResponseDto;
import com.gustavoluz.spendwise_api.entity.User;
import com.gustavoluz.spendwise_api.mapper.UserMapper;
import com.gustavoluz.spendwise_api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserRequestDto dto) {

        User user = userMapper.toEntity(dto);
        User created = userService.create(user);

        return ResponseEntity.ok(userMapper.toDto(created));

    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {

        List<UserResponseDto> users = userService
                .findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();

        if(users.isEmpty()) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity.ok(users);

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {

        User user = userService.findById(id);
        return ResponseEntity.ok(userMapper.toDto(user));

    }

    @PatchMapping("/{id}/name")
    public ResponseEntity<UserResponseDto> updateName(@PathVariable UUID id, @RequestBody String name) {

        User updated = userService.updateName(id, name);
        return ResponseEntity.ok(userMapper.toDto(updated));

    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<UserResponseDto> updateEmail(@PathVariable UUID id, @RequestBody String email) {

        User updated = userService.updateEmail(id, email);
        return ResponseEntity.ok(userMapper.toDto(updated));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        userService.delete(id);
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody UserLoginDto dto, HttpServletResponse response) {

        User user = userMapper.toEntity(dto);

        String token = userService.authenticate(user, response);


        return ResponseEntity.ok(token);
    }

    @GetMapping("/authenticated")
    public ResponseEntity<UserResponseDto> getAuthenticated(HttpServletRequest request) {
        return ResponseEntity
                .ok(userMapper
                        .toDto(userService
                                .getAuthenticated(request)));
    }
}