package com.pe.eph.booking.controller;

import com.pe.eph.booking.dto.request.ClassBookingRequest;
import com.pe.eph.booking.dto.response.ClassBookingResponse;
import com.pe.eph.booking.entity.ClassBooking;
import com.pe.eph.booking.entity.ClassSchedule;
import com.pe.eph.booking.repository.ClassBookingRepository;
import com.pe.eph.booking.repository.ClassScheduleRepository;
import com.pe.eph.booking.service.ClassBookingService;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-bookings")
@RequiredArgsConstructor
public class ClassBookingController {

    private final ClassBookingService classBookingService;
    private final ClassScheduleRepository classScheduleRepository;
    private final ClassBookingRepository classBookingRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/schedules")
    public List<ClassSchedule> getAllSchedules() {
        return classScheduleRepository.findAll();
    }

    @GetMapping
    public List<ClassBooking> getAllBookings(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"))) {
            String phone = authentication.getName();
            Member member = memberRepository.findByPhone(phone)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên"));
            return classBookingRepository.findByMember(member);
        }
        return classBookingRepository.findAll();
    }

    @PostMapping
    public ClassBookingResponse bookClass(@Valid @RequestBody ClassBookingRequest request) {
        return classBookingService.bookClass(request);
    }

    @PutMapping("/{id}/cancel")
    public void cancelBooking(@PathVariable Long id) {
        classBookingService.cancelBooking(id);
    }

}
