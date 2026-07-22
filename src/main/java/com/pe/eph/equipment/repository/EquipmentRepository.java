package com.pe.eph.equipment.repository;

import com.pe.eph.equipment.entity.Equipment;
import com.pe.eph.equipment.entity.EquipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    boolean existsByEquipmentCode(String equipmentCode);

    Page<Equipment> findByNameContainingIgnoreCaseOrEquipmentCodeContainingIgnoreCase(String name, String code, Pageable pageable);

    long countByStatus(EquipmentStatus status);

}
