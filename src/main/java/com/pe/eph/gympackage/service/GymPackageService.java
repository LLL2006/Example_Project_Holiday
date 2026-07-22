package com.pe.eph.gympackage.service;

import com.pe.eph.gympackage.dto.request.CreatePackageRequest;
import com.pe.eph.gympackage.dto.request.UpdatePackageRequest;
import com.pe.eph.gympackage.dto.response.PackageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GymPackageService {

    Page<PackageResponse> getAllPackages(Pageable pageable);

    PackageResponse getPackageById(Long id);

    PackageResponse createPackage(CreatePackageRequest request);

    PackageResponse updatePackage(Long id, UpdatePackageRequest request);

    void deletePackage(Long id);

    Page<PackageResponse> searchPackages(String keyword, Pageable pageable);

}
