package com.pe.eph.booking.controller;

import com.pe.eph.booking.dto.request.PTBookingRequest;
import com.pe.eph.booking.dto.response.PTBookingResponse;
import com.pe.eph.booking.entity.PTBooking;
import com.pe.eph.booking.repository.PTBookingRepository;
import com.pe.eph.booking.service.PTBookingService;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.repository.MemberRepository;
import com.pe.eph.trainer.entity.Trainer;
import com.pe.eph.trainer.repository.TrainerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pt-bookings")
@RequiredArgsConstructor
public class PTBookingController {

    private final PTBookingService ptBookingService;
    private final PTBookingRepository ptBookingRepository;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    @GetMapping
    public List<PTBooking> getAllBookings(Authentication authentication) {
        if (authentication != null) {
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"))) {
                String phone = authentication.getName();
                Member member = memberRepository.findByPhone(phone)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên"));
                return ptBookingRepository.findByMember(member);
            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER"))) {
                String phone = authentication.getName();
                Trainer trainer = trainerRepository.findByPhone(phone)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy huấn luyện viên"));
                return ptBookingRepository.findByTrainer(trainer);
            }
        }
        return ptBookingRepository.findAll();
    }

    @PostMapping
    public PTBookingResponse bookSession(@Valid @RequestBody PTBookingRequest request) {
        return ptBookingService.bookSession(request);
    }

    @PutMapping("/{id}/complete")
    public void completeSession(@PathVariable Long id) {
        ptBookingService.completeSession(id);
    }

    @PutMapping("/{id}/cancel")
    public void cancelSession(@PathVariable Long id) {
        ptBookingService.cancelSession(id);
    }

}
