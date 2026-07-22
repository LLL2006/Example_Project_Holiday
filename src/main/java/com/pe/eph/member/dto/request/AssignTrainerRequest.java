package com.pe.eph.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignTrainerRequest {

    @NotNull(message = "Vui lòng chọn huấn luyện viên")
    private Long trainerId;

}