package com.pe.eph.payment.controller;

import com.pe.eph.payment.dto.request.CreatePaymentRequest;
import com.pe.eph.payment.dto.response.PaymentResponse;
import com.pe.eph.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentService.getAllPayments(pageable);
    }

    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping
    public PaymentResponse createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        return paymentService.createPayment(request);
    }

    @PutMapping("/{id}/confirm")
    public PaymentResponse confirmPayment(@PathVariable Long id) {
        return paymentService.confirmPayment(id);
    }

    @GetMapping("/search")
    public Page<PaymentResponse> searchPayments(
            @RequestParam String keyword,
            Pageable pageable) {

        return paymentService.searchPayments(keyword, pageable);
    }

}