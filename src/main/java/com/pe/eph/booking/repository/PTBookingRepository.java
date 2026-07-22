package com.pe.eph.booking.repository;

import com.pe.eph.booking.entity.PTBooking;
import com.pe.eph.common.enums.PTBookingStatus;
import com.pe.eph.member.entity.Member;
import com.pe.eph.trainer.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PTBookingRepository extends JpaRepository<PTBooking, Long> {

    List<PTBooking> findByMember(Member member);

    List<PTBooking> findByTrainer(Trainer trainer);

    long countByTrainerAndStatus(Trainer trainer, PTBookingStatus status);

    long countBySessionTimeBetween(LocalDateTime start, LocalDateTime end);

    List<PTBooking> findBySessionTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByMemberAndStatus(Member member, PTBookingStatus status);

    List<PTBooking> findByTrainerAndSessionTimeBetween(Trainer trainer, LocalDateTime start, LocalDateTime end);

    List<PTBooking> findByMemberAndSessionTimeBetween(Member member, LocalDateTime start, LocalDateTime end);

}

