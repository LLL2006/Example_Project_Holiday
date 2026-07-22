package com.pe.eph.role.mapper;

import com.pe.eph.role.dto.request.CreateRoleRequest;
import com.pe.eph.role.dto.request.UpdateRoleRequest;
import com.pe.eph.role.dto.response.RoleResponse;
import com.pe.eph.role.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public Role toEntity(CreateRoleRequest request) {
        if (request == null) {
            return null;
        }

        return Role.builder()
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .build();
    }

    public void updateEntity(Role role, UpdateRoleRequest request) {
        if (role == null || request == null) {
            return;
        }

        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
    }

    public RoleResponse toResponse(Role role) {
        if (role == null) {
            return null;
        }

        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .build();
    }

}
