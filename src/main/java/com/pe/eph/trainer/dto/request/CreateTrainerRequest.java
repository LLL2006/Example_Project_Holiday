package com.pe.eph.trainer.dto.request;

import com.pe.eph.common.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTrainerRequest {

    @NotBlank(message = "Mã PT không được để trống")
    @Size(max = 20)
    private String trainerCode;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
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

    @Email
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Chuyên môn không được để trống")
    private String specialization;

    @Min(value = 0)
    private Integer yearsOfExperience;

}