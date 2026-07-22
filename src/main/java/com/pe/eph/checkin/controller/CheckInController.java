package com.pe.eph.checkin.controller;

import com.pe.eph.checkin.dto.request.CheckInRequest;
import com.pe.eph.checkin.dto.response.CheckInResponse;
import com.pe.eph.checkin.entity.CheckInLog;
import com.pe.eph.checkin.repository.CheckInLogRepository;
import com.pe.eph.checkin.service.CheckInService;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.member.dto.response.MemberResponse;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;
    private final MemberRepository memberRepository;
    private final CheckInLogRepository checkInLogRepository;

    @PostMapping
    public CheckInResponse toggleCheckIn(@Valid @RequestBody CheckInRequest request) {
        return checkInService.toggleCheckIn(request);
    }

    @GetMapping("/validate")
    public MemberResponse validateMemberForCheckIn(@RequestParam String memberCode) {
        return checkInService.validateMemberForCheckIn(memberCode);
    }

    @GetMapping("/my-logs")
    public List<CheckInLog> getMyLogs(Authentication authentication) {
        if (authentication == null) {
            throw new ResourceNotFoundException("Chưa đăng nhập");
        }
        String phone = authentication.getName();
        Member member = memberRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên"));
        return checkInLogRepository.findByMemberOrderByCheckInTimeDesc(member);
    }
}
