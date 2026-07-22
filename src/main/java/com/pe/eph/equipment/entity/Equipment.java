package com.pe.eph.equipment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "equipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipment_code", nullable = false, unique = true, length = 20)
    private String equipmentCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @Column(length = 100)
    private String location;

}
