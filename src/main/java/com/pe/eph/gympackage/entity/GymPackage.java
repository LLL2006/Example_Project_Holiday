package com.pe.eph.gympackage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pe.eph.common.enums.PackageStatus;
import com.pe.eph.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gym_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GymPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_code", nullable = false, unique = true, length = 20)
    private String packageCode;

    @Column(name = "package_name", nullable = false, length = 100)
    private String packageName;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageStatus status;

    @Column(name = "pt_sessions")
    private Integer ptSessions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "gymPackage")
    @Builder.Default
    @JsonIgnore
    private List<Member> members = new ArrayList<>();

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
