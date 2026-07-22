package com.pe.eph.user.dto.response;

import com.pe.eph.common.enums.UserStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String username;

    private String fullName;

    private String email;

    private String phone;

    private UserStatus status;

    private String roleName;

}