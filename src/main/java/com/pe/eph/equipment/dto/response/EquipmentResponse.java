package com.pe.eph.equipment.dto.response;

import com.pe.eph.equipment.entity.EquipmentStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentResponse {

    private Long id;
    private String equipmentCode;
    private String name;
    private String type;
    private EquipmentStatus status;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenanceDate;
    private String location;

}
