package com.pe.eph.role.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleRequest {

    @NotBlank(message = "Role không được để trống")
    @Size(max = 50)
    private String roleName;

    @Size(max = 255)
    private String description;

}