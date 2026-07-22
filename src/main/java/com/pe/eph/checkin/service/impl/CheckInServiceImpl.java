package com.pe.eph.checkin.service.impl;

import com.pe.eph.checkin.dto.request.CheckInRequest;
import com.pe.eph.checkin.dto.response.CheckInResponse;
import com.pe.eph.checkin.entity.CheckInLog;
import com.pe.eph.checkin.repository.CheckInLogRepository;
import com.pe.eph.checkin.service.CheckInService;
import com.pe.eph.common.enums.CheckInStatus;
import com.pe.eph.common.enums.MemberStatus;
import com.pe.eph.common.exception.BadRequestException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.member.dto.response.MemberResponse;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.mapper.MemberMapper;
import com.pe.eph.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CheckInServiceImpl implements CheckInService {

    private final CheckInLogRepository checkInLogRepository;
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    public CheckInResponse toggleCheckIn(CheckInRequest request) {
        Member member = memberRepository.findByMemberCode(request.getMemberCode())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên với mã: " + request.getMemberCode()));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new BadRequestException("Tài khoản hội viên không ở trạng thái hoạt động.");
        }

        if (member.getExpireDate() != null && member.getExpireDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Gói tập của hội viên đã hết hạn sử dụng.");
        }

        Optional<CheckInLog> activeLogOpt = checkInLogRepository
                .findTopByMemberAndCheckOutTimeIsNullOrderByCheckInTimeDesc(member);

        CheckInStatus status;
        LocalDateTime timestamp = LocalDateTime.now();
        String photoPath = saveBase64Image(request.getPhotoBase64(), member.getMemberCode());

        if (activeLogOpt.isPresent()) {
            CheckInLog log = activeLogOpt.get();
            log.setCheckOutTime(timestamp);
            if (photoPath != null) {
                log.setPhotoPath(photoPath);
            }
            checkInLogRepository.save(log);
            status = CheckInStatus.CHECK_OUT;
        } else {
            CheckInLog log = CheckInLog.builder()
                    .member(member)
                    .checkInTime(timestamp)
                    .photoPath(photoPath)
                    .build();
            checkInLogRepository.save(log);
            status = CheckInStatus.CHECK_IN;
        }

        return CheckInResponse.builder()
                .memberCode(member.getMemberCode())
                .memberName(member.getFullName())
                .status(status)
                .timestamp(timestamp)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse validateMemberForCheckIn(String memberCode) {
        Member member = memberRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên với mã: " + memberCode));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new BadRequestException("Tài khoản hội viên không ở trạng thái hoạt động.");
        }

        if (member.getExpireDate() != null && member.getExpireDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Gói tập của hội viên đã hết hạn sử dụng.");
        }

        return memberMapper.toResponse(member);
    }

    private String saveBase64Image(String base64Str, String memberCode) {
        if (base64Str == null || base64Str.isBlank()) {
            return null;
        }
        try {
            String base64Data = base64Str;
            if (base64Str.contains(",")) {
                base64Data = base64Str.split(",")[1];
            }
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            Path uploadPath = Paths.get("uploads", "checkin");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = memberCode + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
            Path filePath = uploadPath.resolve(filename);

            Files.write(filePath, imageBytes);
            log.info("Saved check-in photo: {}", filePath.toAbsolutePath());

            return "/uploads/checkin/" + filename;
        } catch (IOException e) {
            log.error("Failed to save check-in image for member {}", memberCode, e);
            return null;
        }
    }
}
