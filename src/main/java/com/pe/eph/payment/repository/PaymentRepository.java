package com.pe.eph.payment.repository;

import com.pe.eph.common.enums.PaymentStatus;
import com.pe.eph.member.entity.Member;
import com.pe.eph.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByInvoiceCode(String invoiceCode);

    Page<Payment> findByMember(Member member, Pageable pageable);

    Page<Payment> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    Page<Payment> findByPaymentDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    Page<Payment> findByInvoiceCodeContaining(String invoiceCode,
                                              Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentStatus = :status AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByPaymentStatusAndPaymentDateBetween(
            @Param("status") PaymentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    List<Payment> findByPaymentStatusAndPaymentDateAfter(PaymentStatus paymentStatus, LocalDateTime startDate);

    List<Payment> findByPaymentStatusAndPaymentDateBetween(PaymentStatus paymentStatus, LocalDateTime startDate, LocalDateTime endDate);

}