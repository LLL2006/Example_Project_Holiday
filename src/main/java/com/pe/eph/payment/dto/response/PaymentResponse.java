package com.pe.eph.payment.dto.response;

import com.pe.eph.common.enums.PaymentMethod;
import com.pe.eph.common.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;

    private String invoiceCode;

    private String memberName;

    private String packageName;

    private BigDecimal amount;

    private BigDecimal remainingValue;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private LocalDateTime paymentDate;

    private String note;

}