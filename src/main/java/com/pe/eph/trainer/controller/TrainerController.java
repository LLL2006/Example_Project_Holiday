package com.pe.eph.trainer.controller;

import com.pe.eph.trainer.dto.request.CreateTrainerRequest;
import com.pe.eph.trainer.dto.request.UpdateTrainerRequest;
import com.pe.eph.trainer.dto.response.TrainerResponse;
import com.pe.eph.trainer.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping
    public Page<TrainerResponse> getAllTrainers(Pageable pageable) {
        return trainerService.getAllTrainers(pageable);
    }

    @GetMapping("/{id}")
    public TrainerResponse getTrainerById(@PathVariable Long id) {
        return trainerService.getTrainerById(id);
    }

    @PostMapping
    public TrainerResponse createTrainer(
            @Valid @RequestBody CreateTrainerRequest request) {

        return trainerService.createTrainer(request);
    }

    @PutMapping("/{id}")
    public TrainerResponse updateTrainer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTrainerRequest request) {

        return trainerService.updateTrainer(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivateTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
    }

    @GetMapping("/search")
    public Page<TrainerResponse> searchTrainer(
            @RequestParam String keyword,
            Pageable pageable) {

        return trainerService.searchTrainers(keyword, pageable);
    }

    @GetMapping("/me")
    public TrainerResponse getTrainerProfile(org.springframework.security.core.Authentication authentication) {
        if (authentication == null) {
            throw new com.pe.eph.common.exception.BadRequestException("Chưa đăng nhập");
        }
        return trainerService.getTrainerByPhone(authentication.getName());
    }

}