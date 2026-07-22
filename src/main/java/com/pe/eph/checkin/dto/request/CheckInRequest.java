package com.pe.eph.checkin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInRequest {

    @NotBlank(message = "Mã hội viên không được để trống")
    private String memberCode;

    private String photoBase64;

}
