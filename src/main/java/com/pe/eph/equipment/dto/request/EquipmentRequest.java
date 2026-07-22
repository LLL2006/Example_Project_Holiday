package com.pe.eph.equipment.dto.request;

import com.pe.eph.equipment.entity.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentRequest {

    @NotBlank(message = "Mã thiết bị không được để trống")
    private String equipmentCode;

    @NotBlank(message = "Tên thiết bị không được để trống")
    private String name;

    private String type;

    @NotNull(message = "Trạng thái thiết bị không được để trống")
    private EquipmentStatus status;

    private LocalDate purchaseDate;

    private LocalDate lastMaintenanceDate;

    private String location;

}
