package com.pe.eph.equipment.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentStatsDto {

    private long totalCount;
    private long operationalCount;
    private long maintenanceCount;
    private long brokenCount;

}
