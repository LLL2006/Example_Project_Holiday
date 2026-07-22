package com.pe.eph.booking.dto.response;

import com.pe.eph.common.enums.ClassBookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassBookingResponse {

    private Long id;
    private String memberName;
    private String className;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String room;
    private ClassBookingStatus status;

}
