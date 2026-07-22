package com.pe.eph.user.service.impl;

import com.pe.eph.common.enums.UserStatus;
import com.pe.eph.common.exception.DuplicateResourceException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.role.entity.Role;
import com.pe.eph.role.repository.RoleRepository;
import com.pe.eph.user.dto.request.CreateUserRequest;
import com.pe.eph.user.dto.request.UpdateUserRequest;
import com.pe.eph.user.dto.response.UserResponse;
import com.pe.eph.user.entity.User;
import com.pe.eph.user.mapper.UserMapper;
import com.pe.eph.user.repository.UserRepository;
import com.pe.eph.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy nhân viên."));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập đã tồn tại.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại.");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy quyền."));

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(role);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy nhân viên."));

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại.");
        }

        if (!user.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại.");
        }

        userMapper.updateEntity(user, request);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy nhân viên."));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String keyword, Pageable pageable) {

        if (keyword == null || keyword.isBlank()) {
            return userRepository.findAll(pageable)
                    .map(userMapper::toResponse);
        }

        return userRepository
                .findByFullNameContainingIgnoreCase(keyword, pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public void changePassword(String username, com.pe.eph.user.dto.request.ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản người dùng."));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
