package com.pe.eph.role.service;

import com.pe.eph.role.dto.request.CreateRoleRequest;
import com.pe.eph.role.dto.request.UpdateRoleRequest;
import com.pe.eph.role.dto.response.RoleResponse;


import java.util.List;

public interface RoleService {

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse createRole(CreateRoleRequest request);

    RoleResponse updateRole(Long id, UpdateRoleRequest request);

    void deleteRole(Long id);

}
