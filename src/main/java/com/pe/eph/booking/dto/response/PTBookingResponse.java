package com.pe.eph.booking.dto.response;

import com.pe.eph.common.enums.PTBookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PTBookingResponse {

    private Long id;
    private String memberName;
    private String trainerName;
    private LocalDateTime sessionTime;
    private PTBookingStatus status;

}
