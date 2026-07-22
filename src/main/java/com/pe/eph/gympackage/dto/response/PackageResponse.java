package com.pe.eph.gympackage.dto.response;

import com.pe.eph.common.enums.PackageStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageResponse {

    private Long id;

    private String packageCode;

    private String packageName;

    private Integer duration;

    private BigDecimal price;

    private String description;

    private PackageStatus status;

    private Integer ptSessions;

}