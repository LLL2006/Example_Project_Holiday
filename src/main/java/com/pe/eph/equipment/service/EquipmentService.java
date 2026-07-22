package com.pe.eph.equipment.service;

import com.pe.eph.equipment.dto.request.EquipmentRequest;
import com.pe.eph.equipment.dto.response.EquipmentResponse;
import com.pe.eph.equipment.dto.response.EquipmentStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipmentService {

    Page<EquipmentResponse> getAllEquipments(Pageable pageable);

    Page<EquipmentResponse> searchEquipments(String keyword, Pageable pageable);

    EquipmentResponse getEquipmentById(Long id);

    EquipmentResponse createEquipment(EquipmentRequest request);

    EquipmentResponse updateEquipment(Long id, EquipmentRequest request);

    void deleteEquipment(Long id);

    EquipmentStatsDto getEquipmentStats();

}
