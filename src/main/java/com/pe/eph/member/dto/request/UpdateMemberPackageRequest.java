package com.pe.eph.member.dto.request;

import com.pe.eph.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberPackageRequest {

    @NotNull(message = "Vui long chon goi tap moi")
    private Long newPackageId;

    private PaymentMethod paymentMethod;

    private String note;
}
