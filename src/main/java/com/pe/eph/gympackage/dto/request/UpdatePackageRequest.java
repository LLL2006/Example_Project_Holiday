package com.pe.eph.gympackage.dto.request;

import com.pe.eph.common.enums.PackageStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePackageRequest {

    @NotBlank
    @Size(max = 100)
    private String packageName;

    @NotNull
    @Min(1)
    private Integer duration;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private String description;

    @NotNull
    private PackageStatus status;

    private Integer ptSessions;

}