package com.pe.eph.member.dto.request;

import com.pe.eph.common.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMemberRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^0[0-9]{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String address;

    private Long packageId;

    private String password;

}