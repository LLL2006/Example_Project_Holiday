package com.pe.eph.booking.entity;

import com.pe.eph.common.enums.PTBookingStatus;
import com.pe.eph.member.entity.Member;
import com.pe.eph.trainer.entity.Trainer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pt_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PTBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "session_time", nullable = false)
    private LocalDateTime sessionTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PTBookingStatus status;

}
