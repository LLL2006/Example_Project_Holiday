package com.pe.eph.gympackage.mapper;

import com.pe.eph.gympackage.dto.request.CreatePackageRequest;
import com.pe.eph.gympackage.dto.request.UpdatePackageRequest;
import com.pe.eph.gympackage.dto.response.PackageResponse;
import com.pe.eph.gympackage.entity.GymPackage;
import org.springframework.stereotype.Component;

@Component
public class GymPackageMapper {

    public GymPackage toEntity(CreatePackageRequest request) {
        if (request == null) {
            return null;
        }

        return GymPackage.builder()
                .packageCode(request.getPackageCode())
                .packageName(request.getPackageName())
                .duration(request.getDuration())
                .price(request.getPrice())
                .description(request.getDescription())
                .ptSessions(request.getPtSessions())
                .build();
    }

    public void updateEntity(GymPackage gymPackage, UpdatePackageRequest request) {
        if (gymPackage == null || request == null) {
            return;
        }

        gymPackage.setPackageName(request.getPackageName());
        gymPackage.setDuration(request.getDuration());
        gymPackage.setPrice(request.getPrice());
        gymPackage.setDescription(request.getDescription());
        gymPackage.setStatus(request.getStatus());
        gymPackage.setPtSessions(request.getPtSessions());
    }

    public PackageResponse toResponse(GymPackage gymPackage) {
        if (gymPackage == null) {
            return null;
        }

        return PackageResponse.builder()
                .id(gymPackage.getId())
                .packageCode(gymPackage.getPackageCode())
                .packageName(gymPackage.getPackageName())
                .duration(gymPackage.getDuration())
                .price(gymPackage.getPrice())
                .description(gymPackage.getDescription())
                .status(gymPackage.getStatus())
                .ptSessions(gymPackage.getPtSessions())
                .build();
    }

}
