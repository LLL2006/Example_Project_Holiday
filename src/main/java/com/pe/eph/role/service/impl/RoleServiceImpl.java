package com.pe.eph.role.service.impl;

import com.pe.eph.common.exception.DuplicateResourceException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.role.dto.request.CreateRoleRequest;
import com.pe.eph.role.dto.request.UpdateRoleRequest;
import com.pe.eph.role.dto.response.RoleResponse;
import com.pe.eph.role.entity.Role;
import com.pe.eph.role.mapper.RoleMapper;
import com.pe.eph.role.repository.RoleRepository;
import com.pe.eph.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    public RoleResponse getRoleById(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy quyền."));

        return roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {

        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new DuplicateResourceException("Tên quyền đã tồn tại.");
        }

        Role role = roleMapper.toEntity(request);

        Role savedRole = roleRepository.save(role);

        return roleMapper.toResponse(savedRole);
    }

    @Override
    public RoleResponse updateRole(Long id, UpdateRoleRequest request) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy quyền."));

        if (!role.getRoleName().equals(request.getRoleName())
                && roleRepository.existsByRoleName(request.getRoleName())) {

            throw new DuplicateResourceException("Tên quyền đã tồn tại.");
        }

        roleMapper.updateEntity(role, request);

        Role updatedRole = roleRepository.save(role);

        return roleMapper.toResponse(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {

        Role role = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy quyền."));

        roleRepository.delete(role);

    }
}