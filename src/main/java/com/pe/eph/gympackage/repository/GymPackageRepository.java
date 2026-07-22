package com.pe.eph.gympackage.repository;

import com.pe.eph.common.enums.PackageStatus;
import com.pe.eph.gympackage.entity.GymPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface GymPackageRepository extends JpaRepository<GymPackage, Long> {

    boolean existsByPackageCode(String packageCode);

    Optional<GymPackage> findByPackageCode(String packageCode);

    Page<GymPackage> findByStatus(PackageStatus status, Pageable pageable);

    Page<GymPackage> findByPackageNameContainingIgnoreCase(String packageName,
                                                           Pageable pageable);

    Page<GymPackage> findByPriceBetween(BigDecimal minPrice,
                                        BigDecimal maxPrice,
                                        Pageable pageable);

}