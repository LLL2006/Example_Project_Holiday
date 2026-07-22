package com.pe.eph.booking.service.impl;

import com.pe.eph.booking.dto.request.PTBookingRequest;
import com.pe.eph.booking.dto.response.PTBookingResponse;
import com.pe.eph.booking.entity.PTBooking;
import com.pe.eph.booking.repository.PTBookingRepository;
import com.pe.eph.booking.service.PTBookingService;
import com.pe.eph.common.enums.MemberStatus;
import com.pe.eph.common.enums.PTBookingStatus;
import com.pe.eph.common.exception.BadRequestException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.member.entity.Member;
import com.pe.eph.member.repository.MemberRepository;
import com.pe.eph.trainer.entity.Trainer;
import com.pe.eph.trainer.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PTBookingServiceImpl implements PTBookingService {

    private final PTBookingRepository ptBookingRepository;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public PTBookingResponse bookSession(PTBookingRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hội viên."));

        if (member.getStatus() != MemberStatus.ACTIVE || member.getExpireDate() == null || member.getExpireDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Tài khoản hội viên đã hết hạn hoặc chưa đăng ký gói tập.");
        }

        long scheduledCount = ptBookingRepository.countByMemberAndStatus(member, PTBookingStatus.SCHEDULED);
        if (member.getRemainingPtSessions() == null || member.getRemainingPtSessions() <= scheduledCount) {
            throw new BadRequestException("Hội viên không có hoặc đã dùng hết số ca tập PT (Hiện có: " 
                    + (member.getRemainingPtSessions() != null ? member.getRemainingPtSessions() : 0) 
                    + " buổi, đã đặt lịch chờ tập: " + scheduledCount + " ca). Vui lòng đăng ký/gia hạn gói tập PT.");
        }

        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy huấn luyện viên (PT)."));

        List<PTBooking> trainerOverlapping = ptBookingRepository.findByTrainerAndSessionTimeBetween(
                trainer,
                request.getSessionTime().minusMinutes(59),
                request.getSessionTime().plusMinutes(59)
        ).stream().filter(b -> PTBookingStatus.SCHEDULED.equals(b.getStatus())).toList();
        if (!trainerOverlapping.isEmpty()) {
            throw new BadRequestException("Huấn luyện viên đang bận dạy ca khác từ " 
                    + trainerOverlapping.get(0).getSessionTime().toLocalTime() 
                    + " đến " + trainerOverlapping.get(0).getSessionTime().plusHours(1).toLocalTime() + ".");
        }

        List<PTBooking> memberOverlapping = ptBookingRepository.findByMemberAndSessionTimeBetween(
                member,
                request.getSessionTime().minusMinutes(59),
                request.getSessionTime().plusMinutes(59)
        ).stream().filter(b -> PTBookingStatus.SCHEDULED.equals(b.getStatus())).toList();
        if (!memberOverlapping.isEmpty()) {
            throw new BadRequestException("Bạn đã có lịch tập PT khác từ " 
                    + memberOverlapping.get(0).getSessionTime().toLocalTime() 
                    + " đến " + memberOverlapping.get(0).getSessionTime().plusHours(1).toLocalTime() + ".");
        }

        if (member.getTrainer() == null) {
            member.setTrainer(trainer);
            memberRepository.save(member);
        }

        PTBooking booking = PTBooking.builder()
                .member(member)
                .trainer(trainer)
                .sessionTime(request.getSessionTime())
                .status(PTBookingStatus.SCHEDULED)
                .build();

        booking = ptBookingRepository.save(booking);

        return PTBookingResponse.builder()
                .id(booking.getId())
                .memberName(member.getFullName())
                .trainerName(trainer.getFullName())
                .sessionTime(booking.getSessionTime())
                .status(booking.getStatus())
                .build();
    }

    @Override
    public void completeSession(Long bookingId) {
        PTBooking booking = ptBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi hẹn tập PT."));

        if (PTBookingStatus.COMPLETED.equals(booking.getStatus())) {
            return;
        }

        Member member = booking.getMember();
        if (member != null) {
            Integer remaining = member.getRemainingPtSessions();
            if (remaining != null && remaining > 0) {
                member.setRemainingPtSessions(remaining - 1);
                memberRepository.save(member);
            }
        }

        booking.setStatus(PTBookingStatus.COMPLETED);
        ptBookingRepository.save(booking);
    }

    @Override
    public void cancelSession(Long bookingId) {
        PTBooking booking = ptBookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy buổi hẹn tập PT."));

        booking.setStatus(PTBookingStatus.CANCELLED);
        ptBookingRepository.save(booking);
    }
}
