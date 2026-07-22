package com.pe.eph.user.controller;

import com.pe.eph.user.dto.request.CreateUserRequest;
import com.pe.eph.user.dto.request.UpdateUserRequest;
import com.pe.eph.user.dto.response.UserResponse;
import com.pe.eph.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivateUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/search")
    public Page<UserResponse> searchUsers(@RequestParam String keyword,
                                          Pageable pageable) {
        return userService.searchUsers(keyword, pageable);
    }

    @PutMapping("/change-password")
    public void changePassword(
            @Valid @RequestBody com.pe.eph.user.dto.request.ChangePasswordRequest request,
            org.springframework.security.core.Authentication authentication) {
        if (authentication == null) {
            throw new org.springframework.security.authentication.BadCredentialsException("Chưa đăng nhập.");
        }
        userService.changePassword(authentication.getName(), request);
    }
}