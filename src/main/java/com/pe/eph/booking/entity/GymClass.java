package com.pe.eph.booking.entity;

import com.pe.eph.trainer.entity.Trainer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gym_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GymClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE, INACTIVE

}
