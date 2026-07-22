package com.pe.eph.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pe.eph.common.enums.Gender;
import com.pe.eph.common.enums.MemberStatus;
import com.pe.eph.gympackage.entity.GymPackage;
import com.pe.eph.payment.entity.Payment;
import com.pe.eph.trainer.entity.Trainer;
import com.pe.eph.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_code", nullable = false, unique = true, length = 20)
    private String memberCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Column(name = "remaining_pt_sessions")
    private Integer remainingPtSessions;

    @Column(length = 255)
    private String avatar;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private GymPackage gymPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @OneToMany(mappedBy = "member",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Payment> payments = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
