package com.pe.eph.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(min = 4, max = 50)
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^0[0-9]{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @NotNull(message = "Role không được để trống")
    private Long roleId;

}