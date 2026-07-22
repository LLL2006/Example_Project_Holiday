package com.pe.eph.payment.dto.request;

import com.pe.eph.common.enums.PaymentMethod;
import com.pe.eph.common.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    private Long memberId;


    @NotNull(message = "Vui lòng chọn gói tập")
    private Long packageId;

    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String note;

    private String avatarBase64;

}
