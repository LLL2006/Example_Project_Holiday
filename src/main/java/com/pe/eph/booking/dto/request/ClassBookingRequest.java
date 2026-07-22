package com.pe.eph.booking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassBookingRequest {

    @NotNull(message = "Vui lòng chọn hội viên")
    private Long memberId;

    @NotNull(message = "Vui lòng chọn lịch lớp học")
    private Long classScheduleId;

}
