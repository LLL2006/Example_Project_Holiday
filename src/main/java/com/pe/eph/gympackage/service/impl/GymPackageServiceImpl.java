package com.pe.eph.gympackage.service.impl;

import com.pe.eph.common.enums.PackageStatus;
import com.pe.eph.common.exception.DuplicateResourceException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.gympackage.dto.request.CreatePackageRequest;
import com.pe.eph.gympackage.dto.request.UpdatePackageRequest;
import com.pe.eph.gympackage.dto.response.PackageResponse;
import com.pe.eph.gympackage.entity.GymPackage;
import com.pe.eph.gympackage.mapper.GymPackageMapper;
import com.pe.eph.gympackage.repository.GymPackageRepository;
import com.pe.eph.gympackage.service.GymPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GymPackageServiceImpl implements GymPackageService {

    private final GymPackageRepository gymPackageRepository;
    private final GymPackageMapper gymPackageMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PackageResponse> getAllPackages(Pageable pageable) {
        return gymPackageRepository.findAll(pageable)
                .map(gymPackageMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PackageResponse getPackageById(Long id) {

        GymPackage gymPackage = gymPackageRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy gói tập."));

        return gymPackageMapper.toResponse(gymPackage);
    }

    @Override
    public PackageResponse createPackage(CreatePackageRequest request) {

        if (gymPackageRepository.existsByPackageCode(request.getPackageCode())) {
            throw new DuplicateResourceException("Mã gói tập đã tồn tại.");
        }

        GymPackage gymPackage = gymPackageMapper.toEntity(request);
        gymPackage.setStatus(PackageStatus.ACTIVE);

        gymPackage = gymPackageRepository.save(gymPackage);

        return gymPackageMapper.toResponse(gymPackage);
    }

    @Override
    public PackageResponse updatePackage(Long id, UpdatePackageRequest request) {

        GymPackage gymPackage = gymPackageRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy gói tập."));

        gymPackageMapper.updateEntity(gymPackage, request);

        gymPackage = gymPackageRepository.save(gymPackage);

        return gymPackageMapper.toResponse(gymPackage);
    }

    @Override
    public void deletePackage(Long id) {

        GymPackage gymPackage = gymPackageRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy gói tập."));

        gymPackage.setStatus(PackageStatus.INACTIVE);

        gymPackageRepository.save(gymPackage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PackageResponse> searchPackages(String keyword, Pageable pageable) {

        return gymPackageRepository
                .findByPackageNameContainingIgnoreCase(keyword, pageable)
                .map(gymPackageMapper::toResponse);
    }
}