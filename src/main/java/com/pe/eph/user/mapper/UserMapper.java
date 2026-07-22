package com.pe.eph.user.mapper;

import com.pe.eph.user.dto.request.CreateUserRequest;
import com.pe.eph.user.dto.request.UpdateUserRequest;
import com.pe.eph.user.dto.response.UserResponse;
import com.pe.eph.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        if (user == null || request == null) {
            return;
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus());
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roleName(user.getRole() == null ? null : user.getRole().getRoleName())
                .build();
    }

}
