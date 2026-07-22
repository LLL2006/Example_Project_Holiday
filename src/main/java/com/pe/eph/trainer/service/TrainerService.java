package com.pe.eph.trainer.service;

import com.pe.eph.trainer.dto.request.CreateTrainerRequest;
import com.pe.eph.trainer.dto.request.UpdateTrainerRequest;
import com.pe.eph.trainer.dto.response.TrainerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainerService {

    Page<TrainerResponse> getAllTrainers(Pageable pageable);

    TrainerResponse getTrainerById(Long id);

    TrainerResponse createTrainer(CreateTrainerRequest request);

    TrainerResponse updateTrainer(Long id, UpdateTrainerRequest request);

    void deleteTrainer(Long id);

    Page<TrainerResponse> searchTrainers(String keyword, Pageable pageable);

    TrainerResponse getTrainerByPhone(String phone);

}
