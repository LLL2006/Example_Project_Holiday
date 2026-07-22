package com.pe.eph.checkin.repository;

import com.pe.eph.checkin.entity.CheckInLog;
import com.pe.eph.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CheckInLogRepository extends JpaRepository<CheckInLog, Long> {

    Optional<CheckInLog> findTopByMemberAndCheckOutTimeIsNullOrderByCheckInTimeDesc(Member member);

    List<CheckInLog> findByMemberOrderByCheckInTimeDesc(Member member);

    long countByCheckOutTimeIsNull();

    List<CheckInLog> findTop5ByOrderByCheckInTimeDesc();

    List<CheckInLog> findByCheckInTimeAfter(LocalDateTime dateTime);

}

