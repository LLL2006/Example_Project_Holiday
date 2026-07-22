package com.pe.eph.user.dto.request;

import com.pe.eph.common.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @Pattern(
            regexp = "^0[0-9]{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @NotNull
    private UserStatus status;

}