package com.pe.eph.booking.controller;

import com.pe.eph.booking.entity.ClassSchedule;
import com.pe.eph.booking.entity.GymClass;
import com.pe.eph.booking.repository.ClassScheduleRepository;
import com.pe.eph.booking.repository.GymClassRepository;
import com.pe.eph.common.exception.ResourceNotFoundException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/class-schedules")
@RequiredArgsConstructor
public class ClassScheduleController {

    private final ClassScheduleRepository classScheduleRepository;
    private final GymClassRepository gymClassRepository;

    @GetMapping
    public List<ClassSchedule> getAllSchedules() {
        return classScheduleRepository.findAll();
    }

    @GetMapping("/{id}")
    public ClassSchedule getScheduleById(@PathVariable Long id) {
        return classScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch học."));
    }

    @PostMapping
    public ClassSchedule createSchedule(@RequestBody ScheduleRequest request) {
        GymClass gymClass = gymClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học."));

        ClassSchedule schedule = ClassSchedule.builder()
                .gymClass(gymClass)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .room(request.getRoom())
                .build();

        return classScheduleRepository.save(schedule);
    }

    @PutMapping("/{id}")
    public ClassSchedule updateSchedule(@PathVariable Long id, @RequestBody ScheduleRequest request) {
        ClassSchedule schedule = classScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch học."));

        GymClass gymClass = gymClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lớp học."));

        schedule.setGymClass(gymClass);
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setRoom(request.getRoom());

        return classScheduleRepository.save(schedule);
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        ClassSchedule schedule = classScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch học."));
        classScheduleRepository.delete(schedule);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ScheduleRequest {
        private Long classId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String room;
    }
}
