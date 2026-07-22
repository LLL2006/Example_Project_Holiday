package com.pe.eph.trainer.dto.response;

import com.pe.eph.common.enums.Gender;
import com.pe.eph.common.enums.UserStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerResponse {

    private Long id;

    private String trainerCode;

    private String fullName;

    private Gender gender;

    private String phone;

    private String email;

    private String specialization;

    private Integer yearsOfExperience;

    private UserStatus status;

    private Integer totalMembers;

    private Long completedSessions;

    private Long scheduledSessions;

}