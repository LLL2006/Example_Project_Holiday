package com.pe.eph.checkin.dto.response;

import com.pe.eph.common.enums.CheckInStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInResponse {

    private String memberCode;
    private String memberName;
    private CheckInStatus status;
    private LocalDateTime timestamp;

}
