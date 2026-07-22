package com.pe.eph.role.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

    private Long id;

    private String roleName;

    private String description;

}