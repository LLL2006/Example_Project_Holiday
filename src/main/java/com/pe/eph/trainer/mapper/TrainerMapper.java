package com.pe.eph.trainer.mapper;

import com.pe.eph.trainer.dto.request.CreateTrainerRequest;
import com.pe.eph.trainer.dto.request.UpdateTrainerRequest;
import com.pe.eph.trainer.dto.response.TrainerResponse;
import com.pe.eph.trainer.entity.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public Trainer toEntity(CreateTrainerRequest request) {
        if (request == null) {
            return null;
        }

        return Trainer.builder()
                .trainerCode(request.getTrainerCode())
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .email(request.getEmail())
                .specialization(request.getSpecialization())
                .yearsOfExperience(request.getYearsOfExperience())
                .build();
    }

    public void updateEntity(Trainer trainer, UpdateTrainerRequest request) {
        if (trainer == null || request == null) {
            return;
        }

        trainer.setFullName(request.getFullName());
        trainer.setGender(request.getGender());
        trainer.setDateOfBirth(request.getDateOfBirth());
        trainer.setPhone(request.getPhone());
        trainer.setEmail(request.getEmail());
        trainer.setSpecialization(request.getSpecialization());
        trainer.setYearsOfExperience(request.getYearsOfExperience());
        trainer.setStatus(request.getStatus());
    }

    public TrainerResponse toResponse(Trainer trainer) {
        if (trainer == null) {
            return null;
        }

        int totalMembers = trainer.getMembers() == null ? 0 : trainer.getMembers().size();

        return TrainerResponse.builder()
                .id(trainer.getId())
                .trainerCode(trainer.getTrainerCode())
                .fullName(trainer.getFullName())
                .gender(trainer.getGender())
                .phone(trainer.getPhone())
                .email(trainer.getEmail())
                .specialization(trainer.getSpecialization())
                .yearsOfExperience(trainer.getYearsOfExperience())
                .status(trainer.getStatus())
                .totalMembers(totalMembers)
                .build();
    }

}
