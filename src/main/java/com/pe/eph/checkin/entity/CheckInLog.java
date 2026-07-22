package com.pe.eph.checkin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pe.eph.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "checkin_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "photo_path", length = 255)
    private String photoPath;

    @PrePersist
    void onCreate() {
        if (checkInTime == null) {
            checkInTime = LocalDateTime.now();
        }
    }
}
