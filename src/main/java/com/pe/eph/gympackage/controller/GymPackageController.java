package com.pe.eph.gympackage.controller;

import com.pe.eph.gympackage.dto.request.CreatePackageRequest;
import com.pe.eph.gympackage.dto.request.UpdatePackageRequest;
import com.pe.eph.gympackage.dto.response.PackageResponse;
import com.pe.eph.gympackage.service.GymPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class GymPackageController {

    private final GymPackageService gymPackageService;

    @GetMapping
    public Page<PackageResponse> getAllPackages(Pageable pageable) {
        return gymPackageService.getAllPackages(pageable);
    }

    @GetMapping("/{id}")
    public PackageResponse getPackageById(@PathVariable Long id) {
        return gymPackageService.getPackageById(id);
    }

    @PostMapping
    public PackageResponse createPackage(
            @Valid @RequestBody CreatePackageRequest request) {

        return gymPackageService.createPackage(request);
    }

    @PutMapping("/{id}")
    public PackageResponse updatePackage(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePackageRequest request) {

        return gymPackageService.updatePackage(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivatePackage(@PathVariable Long id) {
        gymPackageService.deletePackage(id);
    }

    @GetMapping("/search")
    public Page<PackageResponse> searchPackages(
            @RequestParam String keyword,
            Pageable pageable) {

        return gymPackageService.searchPackages(keyword, pageable);
    }

}