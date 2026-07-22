package com.pe.eph.user.service;

import com.pe.eph.user.dto.request.CreateUserRequest;
import com.pe.eph.user.dto.request.UpdateUserRequest;
import com.pe.eph.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    Page<UserResponse> searchUsers(String keyword, Pageable pageable);

    void changePassword(String username, com.pe.eph.user.dto.request.ChangePasswordRequest request);
}
