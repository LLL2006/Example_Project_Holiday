package com.pe.eph.booking.service.impl;

import com.pe.eph.booking.dto.request.ClassBookingRequest;
import com.pe.eph.booking.dto.response.ClassBookingResponse;
import com.pe.eph.booking.entity.ClassBooking;
import com.pe.eph.booking.entity.ClassSchedule;
import com.pe.eph.booking.repository.ClassBookingRepository;
import com.pe.eph.booking.repository.ClassScheduleRepository;
import com.pe.eph.booking.service.ClassBookingService;
import com.pe.eph.common.enums.ClassBookingStatus;
import com.pe.eph.common.enums.MemberStatus;
import com.pe.eph.common.exception.BadRequestException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;  
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassBookingServiceImpl implements ClassBookingService {

    private final ClassBookingRepository classBookingRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final MemberRepository memberRepository;

    @Override
    public ClassBookingResponse bookClass(ClassBookingRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên."));

        if (member.getStatus() != MemberStatus.ACTIVE || member.getExpireDate() == null || member.getExpireDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Tài khoản hội viên đã hết hạn hoặc chưa đăng ký gói tập.");
        }


        ClassSchedule schedule = classScheduleRepository.findById(request.getClassScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch học."));

        if (classBookingRepository.existsByMemberAndClassScheduleAndStatusNot(member, schedule, ClassBookingStatus.CANCELLED)) {
            throw new BadRequestException("Hội viên đã đăng ký lịch học này rồi.");
        }

        long bookedSlots = classBookingRepository.countByClassScheduleAndStatusNot(schedule, ClassBookingStatus.CANCELLED);
        if (bookedSlots >= schedule.getGymClass().getCapacity()) {
            throw new BadRequestException("Lớp học đã đạt số lượng tối đa (" + schedule.getGymClass().getCapacity() + " người).");
        }

        ClassBooking booking = ClassBooking.builder()
                .member(member)
                .classSchedule(schedule)
                .bookingTime(LocalDateTime.now())
                .status(ClassBookingStatus.BOOKED)
                .build();

        booking = classBookingRepository.save(booking);

        return ClassBookingResponse.builder()
                .id(booking.getId())
                .memberName(member.getFullName())
                .className(schedule.getGymClass().getClassName())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .room(schedule.getRoom())
                .status(booking.getStatus())
                .build();
    }

    @Override
    public void cancelBooking(Long bookingId) {
        ClassBooking booking = classBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin lượt đăng ký lớp học."));

        booking.setStatus(ClassBookingStatus.CANCELLED);
        classBookingRepository.save(booking);
    }
}
