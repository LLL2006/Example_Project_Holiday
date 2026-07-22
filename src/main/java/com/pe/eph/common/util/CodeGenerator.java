package com.pe.eph.common.util;

import com.pe.eph.member.repository.MemberRepository;
import com.pe.eph.payment.repository.PaymentRepository;
import com.pe.eph.trainer.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CodeGenerator {

    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final PaymentRepository paymentRepository;

    public String generateMemberCode() {
        long count = memberRepository.count();
        long index = count + 1;
        String code = String.format("MB%04d", index);
        while (memberRepository.existsByMemberCode(code)) {
            index++;
            code = String.format("MB%04d", index);
        }
        return code;
    }

    public String generateTrainerCode() {
        long count = trainerRepository.count();
        long index = count + 1;
        String code = String.format("PT%04d", index);
        while (trainerRepository.existsByTrainerCode(code)) {
            index++;
            code = String.format("PT%04d", index);
        }
        return code;
    }

    public String generateInvoiceCode() {
        long count = paymentRepository.count();
        long index = count + 1;
        String code = String.format("INV%04d", index);
        while (paymentRepository.existsByInvoiceCode(code)) {
            index++;
            code = String.format("INV%04d", index);
        }
        return code;
    }
}
