package com.pe.eph.member.dto.response;

import com.pe.eph.common.enums.Gender;
import com.pe.eph.common.enums.MemberStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private Long id;

    private String memberCode;

    private String fullName;

    private Gender gender;

    private java.time.LocalDate dateOfBirth;

    private String phone;

    private String email;

    private String address;

    private String packageName;

    private String trainerName;

    private Long trainerId;

    private LocalDate joinDate;

    private LocalDate expireDate;

    private MemberStatus status;

    private String avatar;

    private Integer remainingPtSessions;

    private Integer totalPtSessions;

    private Integer activePtBookingsCount;

}