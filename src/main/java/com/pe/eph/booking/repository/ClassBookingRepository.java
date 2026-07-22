package com.pe.eph.booking.repository;

import com.pe.eph.booking.entity.ClassBooking;
import com.pe.eph.booking.entity.ClassSchedule;
import com.pe.eph.common.enums.ClassBookingStatus;
import com.pe.eph.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClassBookingRepository extends JpaRepository<ClassBooking, Long> {

    long countByClassScheduleAndStatusNot(ClassSchedule schedule, ClassBookingStatus status);

    boolean existsByMemberAndClassScheduleAndStatusNot(Member member, ClassSchedule schedule, ClassBookingStatus status);

    List<ClassBooking> findByMember(Member member);

    @Query("SELECT COUNT(cb) FROM ClassBooking cb WHERE cb.classSchedule.startTime BETWEEN :start AND :end AND cb.status <> com.pe.eph.common.enums.ClassBookingStatus.CANCELLED")
    long countTodayBookings(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<ClassBooking> findByMemberAndStatusNot(Member member, ClassBookingStatus status);

}

