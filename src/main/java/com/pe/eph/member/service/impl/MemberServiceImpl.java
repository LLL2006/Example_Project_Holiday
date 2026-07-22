package com.pe.eph.member.service.impl;

import com.pe.eph.common.enums.MemberStatus;
import com.pe.eph.common.enums.PTBookingStatus;
import com.pe.eph.common.enums.PaymentMethod;
import com.pe.eph.common.enums.PaymentStatus;
import com.pe.eph.common.enums.UserStatus;
import com.pe.eph.common.exception.DuplicateResourceException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.common.util.CodeGenerator;
import com.pe.eph.gympackage.entity.GymPackage;
import com.pe.eph.gympackage.repository.GymPackageRepository;
import com.pe.eph.member.dto.request.AssignTrainerRequest;
import com.pe.eph.member.dto.request.CreateMemberRequest;
import com.pe.eph.member.dto.request.UpdateMemberPackageRequest;
import com.pe.eph.member.dto.request.UpdateMemberRequest;
import com.pe.eph.member.dto.response.MemberResponse;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.mapper.MemberMapper;
import com.pe.eph.member.repository.MemberRepository;
import com.pe.eph.member.service.MemberService;
import com.pe.eph.payment.entity.Payment;
import com.pe.eph.payment.repository.PaymentRepository;
import com.pe.eph.role.entity.Role;
import com.pe.eph.role.repository.RoleRepository;
import com.pe.eph.trainer.entity.Trainer;
import com.pe.eph.trainer.repository.TrainerRepository;
import com.pe.eph.user.entity.User;
import com.pe.eph.user.repository.UserRepository;
import com.pe.eph.booking.repository.PTBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final GymPackageRepository gymPackageRepository;
    private final TrainerRepository trainerRepository;
    private final PaymentRepository paymentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodeGenerator codeGenerator;
    private final UserRepository userRepository;
    private final PTBookingRepository ptBookingRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> getAllMembers(Pageable pageable) {

        return memberRepository.findAll(pageable)
                .map(this::enrichResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy hội viên."));

        return enrichResponse(member);
    }

    @Override
    public MemberResponse createMember(CreateMemberRequest request) {

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Số điện thoại đã tồn tại.");
        }

        String finalEmail = request.getEmail() != null && !request.getEmail().isBlank()
                ? request.getEmail()
                : request.getPhone() + "@smartgym.com";

        if (userRepository.existsByEmail(finalEmail)) {
            throw new DuplicateResourceException("Email đã tồn tại.");
        }

        GymPackage gymPackage = null;
        if (request.getPackageId() != null) {
            gymPackage = gymPackageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy gói tập."));
        }

        Role memberRole = roleRepository.findByRoleName("MEMBER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName("MEMBER")
                        .description("Hội viên phòng tập")
                        .build()));

        User user = User.builder()
                .username(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword() != null && !request.getPassword().isBlank() ? request.getPassword() : "123456"))
                .fullName(request.getFullName())
                .email(finalEmail)
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .role(memberRole)
                .build();

        Member member = memberMapper.toEntity(request);
        member.setEmail(finalEmail); 
        member.setMemberCode(codeGenerator.generateMemberCode());
        member.setGymPackage(gymPackage);
        member.setJoinDate(LocalDate.now());
        member.setStartDate(LocalDate.now());
        
        if (gymPackage != null) {
            member.setExpireDate(LocalDate.now().plusMonths(gymPackage.getDuration()));
            member.setStatus(MemberStatus.ACTIVE);
            member.setRemainingPtSessions(gymPackage.getPtSessions() != null ? gymPackage.getPtSessions() : 0);
        } else {
            member.setExpireDate(null);
            member.setStatus(MemberStatus.INACTIVE);
            member.setRemainingPtSessions(0);
        }
        member.setUser(user);

        member = memberRepository.save(member);

        return enrichResponse(member);
    }

    @Override
    public MemberResponse updateMember(Long id, UpdateMemberRequest request) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy hội viên."));

        if (!member.getPhone().equals(request.getPhone())
                && userRepository.existsByPhone(request.getPhone())) {

            throw new DuplicateResourceException("Số điện thoại đã tồn tại.");
        }

        String finalEmail = request.getEmail() != null && !request.getEmail().isBlank()
                ? request.getEmail()
                : request.getPhone() + "@smartgym.com";

        if (!finalEmail.equals(member.getEmail()) && userRepository.existsByEmail(finalEmail)) {
            throw new DuplicateResourceException("Email đã tồn tại.");
        }

        memberMapper.updateEntity(member, request);
        member.setEmail(finalEmail); 

        if (member.getUser() != null) {
            User user = member.getUser();
            user.setPhone(request.getPhone());
            user.setUsername(request.getPhone());
            user.setEmail(finalEmail);
            user.setFullName(request.getFullName());
            userRepository.save(user);
        }

        member = memberRepository.save(member);

        return enrichResponse(member);
    }

    @Override
    public void deleteMember(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy hội viên."));

        member.setStatus(MemberStatus.INACTIVE);

        memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> searchMembers(String keyword, Pageable pageable) {

        return memberRepository
                .searchMembers(keyword, pageable)
                .map(this::enrichResponse);
    }

    @Override
    public MemberResponse assignTrainer(Long memberId, AssignTrainerRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy hội viên."));

        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy huấn luyện viên."));

        member.setTrainer(trainer);
        member = memberRepository.save(member);

        return enrichResponse(member);
    }

    @Override
    public MemberResponse stopTrainer(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy hội viên."));

        member.setTrainer(null);
        member = memberRepository.save(member);

        return enrichResponse(member);
    }

    @Override
    public MemberResponse changePackage(Long memberId, UpdateMemberPackageRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên."));

        GymPackage newPackage = gymPackageRepository.findById(request.getNewPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy gói tập."));

        member.setGymPackage(newPackage);
        member.setStartDate(LocalDate.now());
        member.setExpireDate(LocalDate.now().plusMonths(newPackage.getDuration()));
        member.setStatus(MemberStatus.ACTIVE);
        member.setRemainingPtSessions(newPackage.getPtSessions() != null ? newPackage.getPtSessions() : 0);
        member = memberRepository.save(member);

        Payment payment = Payment.builder()
                .invoiceCode(codeGenerator.generateInvoiceCode())
                .member(member)
                .gymPackage(newPackage)
                .amount(newPackage.getPrice())
                .remainingValue(BigDecimal.ZERO)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.CASH)
                .paymentStatus(PaymentStatus.PAID)
                .paymentDate(LocalDateTime.now())
                .note(request.getNote())
                .build();
        paymentRepository.save(payment);

        return enrichResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberByPhone(String phone) {
        Member member = memberRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên với số điện thoại này: " + phone));
        return enrichResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> getMembersByTrainerPhone(String phone, Pageable pageable) {
        Trainer trainer = trainerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy huấn luyện viên với số điện thoại này: " + phone));
        return memberRepository.findByTrainer(trainer, pageable)
                .map(this::enrichResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> searchMembersByTrainerPhone(String keyword, String phone, Pageable pageable) {
        Trainer trainer = trainerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy huấn luyện viên với số điện thoại này: " + phone));
        return memberRepository.searchMembersByTrainer(trainer, keyword, pageable)
                .map(this::enrichResponse);
    }

    private MemberResponse enrichResponse(Member member) {
        if (member == null) {
            return null;
        }
        MemberResponse response = memberMapper.toResponse(member);
        if (response != null && ptBookingRepository != null) {
            long activeBookings = ptBookingRepository.countByMemberAndStatus(member, PTBookingStatus.SCHEDULED);
            response.setActivePtBookingsCount((int) activeBookings);
        }
        return response;
    }
}