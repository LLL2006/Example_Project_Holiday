package com.pe.eph.booking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PTBookingRequest {

    @NotNull(message = "Vui lòng chọn hội viên")
    private Long memberId;

    @NotNull(message = "Vui lòng chọn huấn luyện viên")
    private Long trainerId;

    @NotNull(message = "Vui lòng chọn thời gian buổi tập")
    private LocalDateTime sessionTime;

}
