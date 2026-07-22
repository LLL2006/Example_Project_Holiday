package com.pe.eph.payment.service;

import com.pe.eph.payment.dto.request.CreatePaymentRequest;
import com.pe.eph.payment.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    Page<PaymentResponse> getAllPayments(Pageable pageable);

    PaymentResponse getPaymentById(Long id);

    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse confirmPayment(Long id);

    Page<PaymentResponse> searchPayments(String keyword,
                                         Pageable pageable);


}
