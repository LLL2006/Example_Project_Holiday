package com.pe.eph.gympackage.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePackageRequest {

    @NotBlank
    @Size(max = 20)
    private String packageCode;

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

    private Integer ptSessions;

}