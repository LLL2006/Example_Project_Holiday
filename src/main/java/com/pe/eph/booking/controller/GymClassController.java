package com.pe.eph.booking.controller;

import com.pe.eph.booking.entity.GymClass;
import com.pe.eph.booking.repository.GymClassRepository;
import com.pe.eph.common.exception.ResourceNotFoundException;
import com.pe.eph.trainer.entity.Trainer;
import com.pe.eph.trainer.repository.TrainerRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class GymClassController {

    private final GymClassRepository gymClassRepository;
    private final TrainerRepository trainerRepository;

    @GetMapping
    public List<GymClass> getAllClasses() {
        return gymClassRepository.findAll();
    }

    @GetMapping("/{id}")
    public GymClass getClassById(@PathVariable Long id) {
        return gymClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học."));
    }

    @PostMapping
    public GymClass createClass(@RequestBody GymClassRequest request) {
        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy huấn luyện viên."));

        GymClass gymClass = GymClass.builder()
                .className(request.getClassName())
                .trainer(trainer)
                .capacity(request.getCapacity())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();

        return gymClassRepository.save(gymClass);
    }

    @PutMapping("/{id}")
    public GymClass updateClass(@PathVariable Long id, @RequestBody GymClassRequest request) {
        GymClass gymClass = gymClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học."));

        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy huấn luyện viên."));

        gymClass.setClassName(request.getClassName());
        gymClass.setTrainer(trainer);
        gymClass.setCapacity(request.getCapacity());
        gymClass.setStatus(request.getStatus());

        return gymClassRepository.save(gymClass);
    }

    @DeleteMapping("/{id}")
    public void deleteClass(@PathVariable Long id) {
        GymClass gymClass = gymClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học."));
        gymClass.setStatus("INACTIVE");
        gymClassRepository.save(gymClass);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GymClassRequest {
        private String className;
        private Long trainerId;
        private Integer capacity;
        private String status;
    }
}
