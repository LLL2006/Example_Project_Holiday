package com.pe.eph.payment.service.impl;

import com.pe.eph.common.enums.MemberStatus;
import com.pe.eph.common.enums.PaymentStatus;
import com.pe.eph.common.exception.BadRequestException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.common.util.CodeGenerator;
import com.pe.eph.gympackage.entity.GymPackage;
import com.pe.eph.gympackage.repository.GymPackageRepository;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.repository.MemberRepository;
import com.pe.eph.payment.dto.request.CreatePaymentRequest;
import com.pe.eph.payment.dto.response.PaymentResponse;
import com.pe.eph.payment.entity.Payment;
import com.pe.eph.payment.mapper.PaymentMapper;
import com.pe.eph.payment.repository.PaymentRepository;
import com.pe.eph.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final GymPackageRepository gymPackageRepository;
    private final PaymentMapper paymentMapper;
    private final CodeGenerator codeGenerator;

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {

        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy hóa đơn."));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false;
        boolean isMember = false;
        boolean isTrainer = false;

        if (auth != null) {
            isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            isMember = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
            isTrainer = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"));
        }

        PaymentStatus status;
        if (isAdmin) {
            status = request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.PAID;
        } else {
            status = PaymentStatus.UNPAID;
            if (isMember) {
                String phone = auth.getName();
                Member currentMember = memberRepository.findByPhone(phone)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin hội viên hiện tại."));
                request.setMemberId(currentMember.getId());
            }
        }

        if (request.getMemberId() == null) {
            throw new BadRequestException("Vui lòng chọn hội viên.");
        }

        Member member = memberRepository.findById(request.getMemberId())

                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy hội viên."));

        GymPackage gymPackage = gymPackageRepository.findById(request.getPackageId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy gói tập."));

        Payment payment = paymentMapper.toEntity(request);

        payment.setMember(member);
        payment.setGymPackage(gymPackage);
        payment.setAmount(gymPackage.getPrice());
        payment.setRemainingValue(BigDecimal.ZERO);
        payment.setPaymentStatus(status);
        if (status == PaymentStatus.PAID) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        payment.setInvoiceCode(codeGenerator.generateInvoiceCode());

        payment = paymentRepository.save(payment);

        if (request.getAvatarBase64() != null && !request.getAvatarBase64().isBlank()) {
            String avatarPath = saveAvatarImage(request.getAvatarBase64(), member.getMemberCode());
            if (avatarPath != null) {
                member.setAvatar(avatarPath);
                memberRepository.save(member);
            }
        }

        if (status == PaymentStatus.PAID) {
            member.setGymPackage(gymPackage);
            LocalDate today = LocalDate.now();
            LocalDate newExpireDate;
            if (member.getExpireDate() != null && member.getExpireDate().isAfter(today) && member.getStatus() == MemberStatus.ACTIVE) {
                newExpireDate = member.getExpireDate().plusMonths(gymPackage.getDuration());
            } else {
                member.setStartDate(today);
                newExpireDate = today.plusMonths(gymPackage.getDuration());
            }
            member.setExpireDate(newExpireDate);
            member.setStatus(MemberStatus.ACTIVE);

            int existingPt = member.getRemainingPtSessions() != null ? member.getRemainingPtSessions() : 0;
            int newPt = gymPackage.getPtSessions() != null ? gymPackage.getPtSessions() : 0;
            member.setRemainingPtSessions(existingPt + newPt);
            memberRepository.save(member);
        }

        return paymentMapper.toResponse(payment);
    }


    @Override
    public PaymentResponse confirmPayment(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new BadRequestException("Bạn không có quyền thực hiện hành động này.");
        }

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn."));

        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Hóa đơn đã được thanh toán.");
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDateTime.now());
        payment = paymentRepository.save(payment);


        Member member = payment.getMember();
        GymPackage gymPackage = payment.getGymPackage();
        if (member != null && gymPackage != null) {
            member.setGymPackage(gymPackage);
            LocalDate today = LocalDate.now();
            LocalDate newExpireDate;
            if (member.getExpireDate() != null && member.getExpireDate().isAfter(today) && member.getStatus() == MemberStatus.ACTIVE) {
                newExpireDate = member.getExpireDate().plusMonths(gymPackage.getDuration());
            } else {
                member.setStartDate(today);
                newExpireDate = today.plusMonths(gymPackage.getDuration());
            }
            member.setExpireDate(newExpireDate);
            member.setStatus(MemberStatus.ACTIVE);

            int existingPt = member.getRemainingPtSessions() != null ? member.getRemainingPtSessions() : 0;
            int newPt = gymPackage.getPtSessions() != null ? gymPackage.getPtSessions() : 0;
            member.setRemainingPtSessions(existingPt + newPt);
            memberRepository.save(member);
        }

        return paymentMapper.toResponse(payment);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> searchPayments(String keyword,
                                                Pageable pageable) {

        return paymentRepository
                .findByInvoiceCodeContaining(keyword, pageable)
                .map(paymentMapper::toResponse);
    }

    private String saveAvatarImage(String base64Str, String memberCode) {
        try {
            String base64Data = base64Str;
            if (base64Str.contains(",")) {
                base64Data = base64Str.split(",")[1];
            }
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            Path uploadPath = Paths.get("uploads", "avatars");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = memberCode + "_avatar_" + System.currentTimeMillis() + ".jpg";
            Path filePath = uploadPath.resolve(filename);

            Files.write(filePath, imageBytes);
            return "/uploads/avatars/" + filename;
        } catch (Exception e) {
            return null;
        }
    }

}