package com.pe.eph.equipment.controller;

import com.pe.eph.equipment.dto.request.EquipmentRequest;
import com.pe.eph.equipment.dto.response.EquipmentResponse;
import com.pe.eph.equipment.dto.response.EquipmentStatsDto;
import com.pe.eph.equipment.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public Page<EquipmentResponse> getAllEquipments(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return equipmentService.searchEquipments(keyword.trim(), pageable);
        }
        return equipmentService.getAllEquipments(pageable);
    }

    @GetMapping("/stats")
    public EquipmentStatsDto getEquipmentStats() {
        return equipmentService.getEquipmentStats();
    }

    @GetMapping("/{id}")
    public EquipmentResponse getEquipmentById(@PathVariable Long id) {
        return equipmentService.getEquipmentById(id);
    }

    @PostMapping
    public EquipmentResponse createEquipment(@Valid @RequestBody EquipmentRequest request) {
        return equipmentService.createEquipment(request);
    }

    @PutMapping("/{id}")
    public EquipmentResponse updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentRequest request) {
        return equipmentService.updateEquipment(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
    }

}
