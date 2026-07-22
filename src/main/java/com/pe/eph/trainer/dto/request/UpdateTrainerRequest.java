package com.pe.eph.trainer.dto.request;

import com.pe.eph.common.enums.Gender;
import com.pe.eph.common.enums.UserStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTrainerRequest {

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotNull
    private Gender gender;

    @NotNull
    private LocalDate dateOfBirth;

    @Pattern(
            regexp = "^0[0-9]{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @Email
    private String email;

    @NotBlank
    private String specialization;

    @Min(0)
    private Integer yearsOfExperience;

    @NotNull
    private UserStatus status;

}