package com.pe.eph.equipment.service.impl;

import com.pe.eph.common.exception.BadRequestException;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.equipment.dto.request.EquipmentRequest;
import com.pe.eph.equipment.dto.response.EquipmentResponse;
import com.pe.eph.equipment.dto.response.EquipmentStatsDto;
import com.pe.eph.equipment.entity.Equipment;
import com.pe.eph.equipment.entity.EquipmentStatus;
import com.pe.eph.equipment.repository.EquipmentRepository;
import com.pe.eph.equipment.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentResponse> getAllEquipments(Pageable pageable) {
        return equipmentRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentResponse> searchEquipments(String keyword, Pageable pageable) {
        return equipmentRepository.findByNameContainingIgnoreCaseOrEquipmentCodeContainingIgnoreCase(keyword, keyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thiết bị."));
        return toResponse(equipment);
    }

    @Override
    public EquipmentResponse createEquipment(EquipmentRequest request) {
        if (equipmentRepository.existsByEquipmentCode(request.getEquipmentCode())) {
            throw new BadRequestException("Mã thiết bị đã tồn tại.");
        }

        Equipment equipment = Equipment.builder()
                .equipmentCode(request.getEquipmentCode())
                .name(request.getName())
                .type(request.getType())
                .status(request.getStatus())
                .purchaseDate(request.getPurchaseDate())
                .lastMaintenanceDate(request.getLastMaintenanceDate())
                .location(request.getLocation())
                .build();

        equipment = equipmentRepository.save(equipment);
        return toResponse(equipment);
    }

    @Override
    public EquipmentResponse updateEquipment(Long id, EquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thiết bị."));

        if (!equipment.getEquipmentCode().equals(request.getEquipmentCode()) &&
                equipmentRepository.existsByEquipmentCode(request.getEquipmentCode())) {
            throw new BadRequestException("Mã thiết bị đã tồn tại.");
        }

        equipment.setEquipmentCode(request.getEquipmentCode());
        equipment.setName(request.getName());
        equipment.setType(request.getType());
        equipment.setStatus(request.getStatus());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setLastMaintenanceDate(request.getLastMaintenanceDate());
        equipment.setLocation(request.getLocation());

        equipment = equipmentRepository.save(equipment);
        return toResponse(equipment);
    }

    @Override
    public void deleteEquipment(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thiết bị."));
        equipmentRepository.delete(equipment);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentStatsDto getEquipmentStats() {
        long total = equipmentRepository.count();
        long operational = equipmentRepository.countByStatus(EquipmentStatus.OPERATIONAL);
        long maintenance = equipmentRepository.countByStatus(EquipmentStatus.MAINTENANCE);
        long broken = equipmentRepository.countByStatus(EquipmentStatus.BROKEN);

        return EquipmentStatsDto.builder()
                .totalCount(total)
                .operationalCount(operational)
                .maintenanceCount(maintenance)
                .brokenCount(broken)
                .build();
    }

    private EquipmentResponse toResponse(Equipment e) {
        return EquipmentResponse.builder()
                .id(e.getId())
                .equipmentCode(e.getEquipmentCode())
                .name(e.getName())
                .type(e.getType())
                .status(e.getStatus())
                .purchaseDate(e.getPurchaseDate())
                .lastMaintenanceDate(e.getLastMaintenanceDate())
                .location(e.getLocation())
                .build();
    }
}
