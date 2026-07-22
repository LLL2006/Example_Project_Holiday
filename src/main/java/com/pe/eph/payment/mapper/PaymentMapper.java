package com.pe.eph.payment.mapper;

import com.pe.eph.payment.dto.request.CreatePaymentRequest;
import com.pe.eph.payment.dto.response.PaymentResponse;
import com.pe.eph.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(CreatePaymentRequest request) {
        if (request == null) {
            return null;
        }

        return Payment.builder()
                .paymentMethod(request.getPaymentMethod())
                .note(request.getNote())
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .invoiceCode(payment.getInvoiceCode())
                .memberName(payment.getMember() == null ? null : payment.getMember().getFullName())
                .packageName(payment.getGymPackage() == null ? null : payment.getGymPackage().getPackageName())
                .amount(payment.getAmount())
                .remainingValue(payment.getRemainingValue())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .paymentDate(payment.getPaymentDate())
                .note(payment.getNote())
                .build();
    }

}
